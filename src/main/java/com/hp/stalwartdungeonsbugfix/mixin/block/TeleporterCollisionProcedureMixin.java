package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.block.SdBlockLogicHelper;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.procedures.TeleporterBlockEntityCollidesInTheBlockProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复传送器碰撞触发过程：消除 22 个匿名类与 18 次 getOrCreateTag 分配、仅服务端运行、避免强加载未加载区块与连续自伤。
 */
@Mixin(value = TeleporterBlockEntityCollidesInTheBlockProcedure.class, remap = false)
public abstract class TeleporterCollisionProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeTeleporterCollision(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixTrapBlocksAndTeleporterEnabled()) {
            return;
        }
        SdBlockLogicHelper.handleTeleporterCollision(world, x, y, z, entity);
        ci.cancel();
    }
}
