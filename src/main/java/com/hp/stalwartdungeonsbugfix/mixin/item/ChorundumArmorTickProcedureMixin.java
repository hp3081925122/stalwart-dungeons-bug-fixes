package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.item.SdItemLogicHelper;
import net.mcreator.stalwartdungeons.procedures.ChorundumArmorBodyTickEventProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复紫颂锭套装每刻无条件调用 removeEffect(LEVITATION) 引发的客户端/服务端事件风暴。
 */
@Mixin(value = ChorundumArmorBodyTickEventProcedure.class, remap = false)
public abstract class ChorundumArmorTickProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeChorundumArmorTick(Entity entity, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixBossAndMobProceduresEnabled()) {
            return;
        }
        SdItemLogicHelper.handleChorundumArmorTick(entity);
        ci.cancel();
    }
}
