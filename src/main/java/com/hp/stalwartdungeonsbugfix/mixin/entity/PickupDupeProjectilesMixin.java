package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.entity.AwfulDungeonSpawnerEntity;
import net.mcreator.stalwartdungeons.entity.EndDungeonSpawnerEntity;
import net.mcreator.stalwartdungeons.entity.KeepingCastleSpawnerEntity;
import net.mcreator.stalwartdungeons.entity.SporeBottleEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 4 种一次性消耗投掷物（三座地牢刷怪笼投掷物 + 孢子瓶）未禁止箭矢拾取导致近距/盾反刷物品漏洞，
 * 并确保 onHitBlock 仅在服务端触发。
 */
@Mixin({
        AwfulDungeonSpawnerEntity.class,
        EndDungeonSpawnerEntity.class,
        KeepingCastleSpawnerEntity.class,
        SporeBottleEntity.class
})
public abstract class PickupDupeProjectilesMixin extends AbstractArrow {
    protected PickupDupeProjectilesMixin(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void sd$disallowPickup(CallbackInfo ci) {
        if (SdBugfixConfig.isFixEntityCrashesAndAiEnabled() && this.pickup != Pickup.DISALLOWED) {
            this.pickup = Pickup.DISALLOWED;
        }
    }

    @Inject(method = "onHitBlock", at = @At("HEAD"), cancellable = true)
    private void sd$serverOnlyOnHitBlock(BlockHitResult blockHitResult, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixEntityCrashesAndAiEnabled()) {
            return;
        }
        this.pickup = Pickup.DISALLOWED;
        if (this.level().isClientSide()) {
            super.onHitBlock(blockHitResult);
            ci.cancel();
        }
    }
}
