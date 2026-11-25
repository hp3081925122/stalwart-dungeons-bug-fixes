package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.entity.ShulkerCannonEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复潜影加农炮右键骑乘在双端无条件调用 startRiding 且不检查是否已有骑手的问题。
 */
@Mixin(ShulkerCannonEntity.class)
public abstract class ShulkerCannonEntityMixin extends PathfinderMob {
    protected ShulkerCannonEntityMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void sd$safeMountShulkerCannon(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!SdBugfixConfig.isFixEntityCrashesAndAiEnabled()) {
            return;
        }
        if (this.isVehicle()) {
            cir.setReturnValue(InteractionResult.PASS);
            return;
        }
        if (!this.level().isClientSide()) {
            player.startRiding(this);
        }
        cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide()));
    }
}
