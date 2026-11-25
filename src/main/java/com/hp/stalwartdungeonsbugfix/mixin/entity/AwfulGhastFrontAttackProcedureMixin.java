package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.entity.SdEntityLogicHelper;
import net.mcreator.stalwartdungeons.procedures.AwfulGhastFrontAttackProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复可怕恶魂半血冲锋阶段判定：将硬编码 100.0 改为 50% 最大生命值，兼容整合包自定义 Boss 血量。
 */
@Mixin(value = AwfulGhastFrontAttackProcedure.class, remap = false)
public abstract class AwfulGhastFrontAttackProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$percentageFrontAttack(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!SdBugfixConfig.isFixBossAndMobProceduresEnabled()) {
            return;
        }
        cir.setReturnValue(SdEntityLogicHelper.isAwfulGhastLowHealth(entity));
    }
}
