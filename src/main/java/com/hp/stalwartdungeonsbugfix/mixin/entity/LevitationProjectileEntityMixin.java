package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.entity.LevitationProjectileEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复漂浮投射物命中实体不给飘浮效果的问题（显式挂钩 AbstractArrow.onHitEntity 以保证 SRG 映射可靠）。
 */
@Mixin(AbstractArrow.class)
public abstract class LevitationProjectileEntityMixin {
    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void sd$applyLevitationOnHit(EntityHitResult hitResult, CallbackInfo ci) {
        if (!((Object) this instanceof LevitationProjectileEntity self) || !SdBugfixConfig.isFixEntityCrashesAndAiEnabled()) {
            return;
        }
        if (self.level().isClientSide()) {
            return;
        }
        Entity target = hitResult.getEntity();
        if (target instanceof LivingEntity living) {
            if (living instanceof Player player && (player.isCreative() || player.isSpectator())) {
                return;
            }
            living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 1));
        }
    }
}
