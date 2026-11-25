package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.entity.SdEntityLogicHelper;
import net.mcreator.stalwartdungeons.procedures.SheltererEntityDiesProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复庇护者死亡转二阶段：保留玩家附近掉落物并加入幂等锁防止重复刷出二阶段 Boss。
 */
@Mixin(value = SheltererEntityDiesProcedure.class, remap = false)
public abstract class SheltererDiesProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeSheltererDies(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixEntityCrashesAndAiEnabled()) {
            return;
        }
        SdEntityLogicHelper.handleSheltererDies(world, x, y, z, entity);
        ci.cancel();
    }
}
