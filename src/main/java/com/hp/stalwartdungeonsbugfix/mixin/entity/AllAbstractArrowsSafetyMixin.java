package com.hp.stalwartdungeonsbugfix.mixin.entity;

import net.mcreator.stalwartdungeons.entity.AwfulDungeonSpawnerEntity;
import net.mcreator.stalwartdungeons.entity.AwfulGunEntity;
import net.mcreator.stalwartdungeons.entity.CannonPrEntity;
import net.mcreator.stalwartdungeons.entity.ChorundumShieldEntity;
import net.mcreator.stalwartdungeons.entity.EndDungeonSpawnerEntity;
import net.mcreator.stalwartdungeons.entity.KeepingCastleSpawnerEntity;
import net.mcreator.stalwartdungeons.entity.LevitationProjectileEntity;
import net.mcreator.stalwartdungeons.entity.SporeBottleEntity;
import net.mcreator.stalwartdungeons.entity.TungstenShieldEntity;
import net.mcreator.stalwartdungeons.entity.WartedTungstenShieldEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 为坚固地牢全部 10 种 AbstractArrow 投射物统一补齐安全兜底：
 * 1. 禁止玩家地面/盾反拾取（Pickup.DISALLOWED）；
 * 2. 防止 doPostHurtEffects 中 setArrowCount(getArrowCount() - 1) 下溢为负数；
 * 3. 增加 400 tick 服务端最大飞行存活上限，防止飞入天空或虚空永久滞留泄漏实体。
 */
@Mixin({
        AwfulDungeonSpawnerEntity.class,
        AwfulGunEntity.class,
        CannonPrEntity.class,
        ChorundumShieldEntity.class,
        EndDungeonSpawnerEntity.class,
        KeepingCastleSpawnerEntity.class,
        LevitationProjectileEntity.class,
        SporeBottleEntity.class,
        TungstenShieldEntity.class,
        WartedTungstenShieldEntity.class
})
public abstract class AllAbstractArrowsSafetyMixin extends AbstractArrow {
    protected AllAbstractArrowsSafetyMixin(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void sd$enforcePickupDisallowedAndLifetime(CallbackInfo ci) {
        if (this.pickup != Pickup.DISALLOWED) {
            this.pickup = Pickup.DISALLOWED;
        }
        if (!this.level().isClientSide() && this.tickCount >= 400) {
            this.discard();
        }
    }

    @Redirect(
            method = "doPostHurtEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;setArrowCount(I)V"
            )
    )
    private void sd$clampArrowCountUnderflow(LivingEntity entity, int count) {
        entity.setArrowCount(Math.max(0, count));
    }
}
