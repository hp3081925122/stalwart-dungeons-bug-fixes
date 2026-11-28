package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.entity.ShulkerCannonEntity;
import net.mcreator.stalwartdungeons.entity.SporeProjectileEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 修复潜影加农炮与孢子投射物在 1.20.1 中仅判 DamageTypes.EXPLOSION 而漏判 DamageTypes.PLAYER_EXPLOSION 的问题（MCR-60）。
 * 改由 DamageTypeTags.IS_EXPLOSION 统一匹配各类爆炸伤害，同时放行绕过无敌（如 /kill）的伤害来源。
 */
@Mixin({
        ShulkerCannonEntity.class,
        SporeProjectileEntity.class
})
public abstract class ExplosionImmunityTagFixMixin {
    @Redirect(
            method = "hurt",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/resources/ResourceKey;)Z"
            )
    )
    private boolean sd$matchAllExplosionDamageTypes(DamageSource source, ResourceKey<DamageType> key) {
        if (SdBugfixConfig.isFixEntityCrashesAndAiEnabled() && key == DamageTypes.EXPLOSION) {
            return !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && source.is(DamageTypeTags.IS_EXPLOSION);
        }
        return source.is(key);
    }
}
