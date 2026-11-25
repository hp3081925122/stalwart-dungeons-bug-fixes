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
        if (!SdBugfixConfig.isFixItemProceduresAndMechanicsEnabled()) {
            return;
        }
        SdItemLogicHelper.handleShieldAttackEvent(event instanceof LivingAttackEvent lae ? lae : null, entity, amount);
        ci.cancel();
    }
}
