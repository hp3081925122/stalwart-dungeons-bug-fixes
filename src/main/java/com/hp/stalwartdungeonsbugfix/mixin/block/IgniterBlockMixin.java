package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.block.IgniterBlock;
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
 * 取消点燃器仅用于发送灵魂火焰粒子的服务端永久 10t 定时任务与发包，视觉粒子由 IgniterAnimateTickMixin 在客户端本地生成。
 */
@Mixin(IgniterBlock.class)
public abstract class IgniterBlockMixin extends Block {
    private IgniterBlockMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "onPlace", at = @At("HEAD"), cancellable = true)
    private void sd$skipServerParticleSchedule(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving, CallbackInfo ci) {
        if (!SdBugfixConfig.isOptimizeIgniterClientParticlesEnabled()) {
            return;
        }
        super.onPlace(blockstate, world, pos, oldState, moving);
        ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void sd$stopLegacyServerParticleLoop(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!SdBugfixConfig.isOptimizeIgniterClientParticlesEnabled()) {
            return;
        }
        super.tick(blockstate, world, pos, random);
        ci.cancel();
    }
}
