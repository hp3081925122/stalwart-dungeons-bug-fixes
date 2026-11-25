package com.hp.stalwartdungeonsbugfix.mixin.world;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.world.SdWorldgenHelper;
import net.mcreator.stalwartdungeons.procedures.AwfulDungeonsSpawnBlockAddedProcedure;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 优化可怕地牢触发方块与刷怪笼放置：仅服务端执行、先移除触发块再放置、忽略结构方块、保护基岩、使用 flag=2 减少级联卡顿。
 */
@Mixin(value = AwfulDungeonsSpawnBlockAddedProcedure.class, remap = false)
public abstract class AwfulDungeonsSpawnProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeSpawnAwfulDungeon(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        if (!SdBugfixConfig.isOptimizeDungeonStructurePlacementEnabled()) {
            return;
        }
        SdWorldgenHelper.placeAwfulDungeonFromTrigger(world, x, y, z);
        ci.cancel();
    }
}
