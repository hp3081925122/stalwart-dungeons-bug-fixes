package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.entity.SdEntityLogicHelper;
import net.mcreator.stalwartdungeons.procedures.SporeBottleBulletHitsBlockProcedure;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复孢子瓶落地音效与粒子误放在实体遍历循环内导致空地无声无粒子、人群 N 倍刷屏的问题。
 */
@Mixin(value = SporeBottleBulletHitsBlockProcedure.class, remap = false)
public abstract class SporeBottleHitsBlockProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeSporeBottleHit(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixBossAndMobProceduresEnabled()) {
            return;
        }
        SdEntityLogicHelper.handleSporeBottleHitsBlock(world, x, y, z);
        ci.cancel();
    }
}
