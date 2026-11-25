package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.entity.SdEntityLogicHelper;
import net.mcreator.stalwartdungeons.procedures.AwfulGhastShootAtDistanceProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复可怕恶魂半血远程阶段判定：将硬编码 100.0 改为 50% 最大生命值。
 */
@Mixin(value = AwfulGhastShootAtDistanceProcedure.class, remap = false)
public abstract class AwfulGhastShootAtDistanceProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$percentageShootAtDistance(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!SdBugfixConfig.isFixBossAndMobProceduresEnabled()) {
            return;
        }
        cir.setReturnValue(entity != null && !SdEntityLogicHelper.isAwfulGhastLowHealth(entity));
    }
}
