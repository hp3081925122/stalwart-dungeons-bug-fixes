package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.item.SdItemLogicHelper;
import net.mcreator.stalwartdungeons.procedures.SdShieldPrProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/**
 * 修复三面自定义盾牌的 LivingAttackEvent 耐久消耗逻辑：仅服务端运行、尊重创造模式与可格挡伤害类型。
 */
@Mixin(value = SdShieldPrProcedure.class, remap = false)
public abstract class SdShieldPrProcedureMixin {
    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;D)V", at = @At("HEAD"), cancellable = true)
    private static void sd$safeShieldDurability(@Nullable Event event, Entity entity, double amount, CallbackInfo ci) {
        // 当启用原生盾牌格挡修复时，原版 Player#hurtCurrentlyUsedShield 已按格挡角度、耐久附魔与 ShieldBlockEvent 精准扣减耐久，
        // 跳过原模组在 LivingAttackEvent 中重复手写扣耐久的逻辑，防止单次格挡双倍掉耐久。
        if (SdBugfixConfig.isFixCustomShieldsEnabled()) {
            ci.cancel();
            return;
        }
        if (!SdBugfixConfig.isFixItemProceduresAndMechanicsEnabled()) {
            return;
        }
        SdItemLogicHelper.handleShieldAttackEvent(event instanceof LivingAttackEvent lae ? lae : null, entity, amount);
        ci.cancel();
    }
}
