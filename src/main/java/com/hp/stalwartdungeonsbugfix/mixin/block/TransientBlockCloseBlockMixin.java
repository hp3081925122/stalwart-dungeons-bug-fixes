package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.block.SdBlockLogicHelper;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.block.TransientBlockCloseBlock;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModBlocks;
import net.mcreator.stalwartdungeons.procedures.TransientBlockCloseRedstoneOnProcedure;
import net.mcreator.stalwartdungeons.procedures.TransientBlockCloseUpdateTickProcedure;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 优化关闭态瞬态方块（守望城堡每座含 554 块）：
 * 取消闲置时的永久 10t 定时轮询，改为仅在邻接方块变为开启态或手动紧挨开启态放置时调度单次 10t 传播，
 * 在保持 100% 链式开启玩法不变的前提下彻底消除每座城堡 1108 次/秒的无用方块 tick 卡顿。
 */
@Mixin(TransientBlockCloseBlock.class)
public abstract class TransientBlockCloseBlockMixin extends Block {
    private TransientBlockCloseBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "onPlace", at = @At("HEAD"), cancellable = true)
    private void sd$skipIdleScheduleOnPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving, CallbackInfo ci) {
        if (!SdBugfixConfig.isOptimizeTransientBlockTickingEnabled()) {
            return;
        }
        super.onPlace(blockstate, world, pos, oldState, moving);
        // 仅在非“刚从开启态关闭”且紧挨开启态瞬态方块时才安排传播检测，避免刚关闭时互相震荡重开
        if (!world.isClientSide()
                && !oldState.is(StalwartDungeonsModBlocks.TRANSIENT_BLOCK_OPEN.get())
                && SdBlockLogicHelper.hasOpenTransientNeighbor(world, pos)) {
            world.scheduleTick(pos, this, 10);
        }
        ci.cancel();
    }

    @Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
    private void sd$schedulePropagationOnNeighborOpen(
            BlockState blockstate,
            Level world,
            BlockPos pos,
            Block neighborBlock,
            BlockPos fromPos,
            boolean moving,
            CallbackInfo ci
    ) {
        if (!SdBugfixConfig.isOptimizeTransientBlockTickingEnabled()) {
            return;
        }
        super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
        if (!world.isClientSide()) {
            if (world.getBestNeighborSignal(pos) > 0) {
                TransientBlockCloseRedstoneOnProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
            } else if (world.getBlockState(fromPos).is(StalwartDungeonsModBlocks.TRANSIENT_BLOCK_OPEN.get())) {
                world.scheduleTick(pos, this, 10);
            }
        }
        ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void sd$tickOnceWithoutIdleLoop(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!SdBugfixConfig.isOptimizeTransientBlockTickingEnabled()) {
            return;
        }
        super.tick(blockstate, world, pos, random);
        TransientBlockCloseUpdateTickProcedure.execute(world, pos.getX(), pos.getY(), pos.getZ());
        // 不再无条件重新 scheduleTick，彻底结束闲置轮询
        ci.cancel();
    }
}
