package com.hp.stalwartdungeonsbugfix.mixin.core;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.core.SdEffectRenewalGuard;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/**
 * 为药水刷新（onEffectUpdated）与提前净化清除（onEffectRemoved 且剩余时长 > 0）打上线程局部守卫，
 * 防止坚固地牢的孢子效果在续期或被牛奶/不死图腾/指令清除时错误触发 removeAttributeModifiers 生成下界疣茧。
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityEffectGuardMixin {
    @Inject(method = "onEffectUpdated", at = @At("HEAD"))
    private void sd$enterEffectRefresh(MobEffectInstance effect, boolean forced, @Nullable Entity source, CallbackInfo ci) {
        if (SdBugfixConfig.isFixBossAndMobProceduresEnabled()) {
            SdEffectRenewalGuard.enterRefresh();
        }
    }

    @Inject(method = "onEffectUpdated", at = @At("RETURN"))
    private void sd$exitEffectRefresh(MobEffectInstance effect, boolean forced, @Nullable Entity source, CallbackInfo ci) {
        if (SdEffectRenewalGuard.isRefreshing()) {
            SdEffectRenewalGuard.exitRefresh();
        }
    }

    @Inject(method = "onEffectRemoved", at = @At("HEAD"))
    private void sd$enterEarlyEffectRemoval(MobEffectInstance effect, CallbackInfo ci) {
        if (SdBugfixConfig.isFixBossAndMobProceduresEnabled() && effect != null && effect.getDuration() > 0) {
            SdEffectRenewalGuard.enterEarlyRemoval();
        }
    }

    @Inject(method = "onEffectRemoved", at = @At("RETURN"))
    private void sd$exitEarlyEffectRemoval(MobEffectInstance effect, CallbackInfo ci) {
        if (SdEffectRenewalGuard.isEarlyRemoval()) {
            SdEffectRenewalGuard.exitEarlyRemoval();
        }
    }
}
