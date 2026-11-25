package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.item.SdItemLogicHelper;
import net.mcreator.stalwartdungeons.procedures.TungstenBodyTickEventProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复钨套装每刻无条件调用 removeEffect 与 clearFire 引发的事件风暴。
 */
@Mixin(value = TungstenBodyTickEventProcedure.class, remap = false)
public abstract class TungstenArmorTickProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeTungstenArmorTick(Entity entity, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixBossAndMobProceduresEnabled()) {
            return;
        }
        SdItemLogicHelper.handleTungstenArmorTick(entity);
        ci.cancel();
    }
}
