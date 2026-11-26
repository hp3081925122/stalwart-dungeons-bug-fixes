package com.hp.stalwartdungeonsbugfix.mixin.block;

import io.netty.buffer.Unpooled;
import net.mcreator.stalwartdungeons.block.TeleporterBlockBlock;
import net.mcreator.stalwartdungeons.world.inventory.TeleporterGUIMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 将打开传送器界面时 MenuProvider 写死的英文标题 "Teleporter" 改为对应方块的原生翻译键。
 */
@Mixin(TeleporterBlockBlock.class)
public abstract class TeleporterBlockUseTitleMixin extends Block {
    protected TeleporterBlockUseTitleMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void sd$openLocalizedTeleporterScreen(
            BlockState state,
            Level world,
            BlockPos pos,
            Player entity,
            InteractionHand hand,
            BlockHitResult hit,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        super.use(state, world, pos, entity, hand, hit);
        if (entity instanceof ServerPlayer player) {
            NetworkHooks.openScreen(
                    player,
                    new SimpleMenuProvider(
                            (id, inventory, p) -> new TeleporterGUIMenu(
                                    id,
                                    inventory,
                                    new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos)
                            ),
                            this.getName()
                    ),
                    pos
            );
        }
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
