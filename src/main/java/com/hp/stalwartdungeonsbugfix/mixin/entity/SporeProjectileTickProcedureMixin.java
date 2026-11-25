package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.entity.SdEntityLogicHelper;
import net.mcreator.stalwartdungeons.procedures.SporeOnEntityTickUpdateProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复孢子投射物落地 discard 缺 return 与自我感染孢子效果问题。
 */
@Mixin(value = SporeOnEntityTickUpdateProcedure.class, remap = false)
public abstract class SporeProjectileTickProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeSporeProjectileTick(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixBossAndMobProceduresEnabled()) {
            return;
        }
        SdEntityLogicHelper.handleSporeProjectileTick(world, x, y, z, entity);
        ci.cancel();
    }
}
