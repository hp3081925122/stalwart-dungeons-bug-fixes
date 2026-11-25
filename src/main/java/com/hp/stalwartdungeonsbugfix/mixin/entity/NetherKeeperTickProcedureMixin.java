package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.entity.SdEntityLogicHelper;
import net.mcreator.stalwartdungeons.procedures.NetherKeeperOnEntityTickUpdateProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复下界守卫每刻逻辑：百分比血量阈值、眩晕烈焰人数量上限、修复 3 处错误火焰粒子坐标、驯服不感染主人。
 */
@Mixin(value = NetherKeeperOnEntityTickUpdateProcedure.class, remap = false)
public abstract class NetherKeeperTickProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeNetherKeeperTick(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixBossAndMobProceduresEnabled()) {
            return;
        }
        SdEntityLogicHelper.handleNetherKeeperTick(world, x, y, z, entity);
        ci.cancel();
    }
}
