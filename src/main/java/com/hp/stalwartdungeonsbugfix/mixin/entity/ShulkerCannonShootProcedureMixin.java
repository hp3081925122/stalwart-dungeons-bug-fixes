package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.entity.SdEntityLogicHelper;
import net.mcreator.stalwartdungeons.procedures.ShulkerCannonShootProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

/**
 * 修复骑乘潜影加农炮开炮：校验 TNT 冷却、仅服务端执行、创造不耗弹药并将 3 次重复射线检测合并为 1 次。
 */
@Mixin(value = ShulkerCannonShootProcedure.class, remap = false)
public abstract class ShulkerCannonShootProcedureMixin {
    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void sd$safeShulkerCannonShoot(@Nullable Event event, LevelAccessor world, Entity entity, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixBossAndMobProceduresEnabled()) {
            return;
        }
        SdEntityLogicHelper.handleShulkerCannonShoot(world, entity);
        ci.cancel();
    }
}
