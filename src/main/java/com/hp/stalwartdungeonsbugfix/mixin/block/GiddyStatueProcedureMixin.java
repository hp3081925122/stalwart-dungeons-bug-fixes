package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.block.SdBlockLogicHelper;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.procedures.GiddyStatueUpdateTickProcedure;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复眩晕雕像触发过程：仅服务端执行并排除旁观者玩家。
 */
@Mixin(value = GiddyStatueUpdateTickProcedure.class, remap = false)
public abstract class GiddyStatueProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeGiddyStatueTick(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixTrapBlocksAndTeleporterEnabled()) {
            return;
        }
        SdBlockLogicHelper.handleGiddyStatueTick(world, x, y, z);
        ci.cancel();
    }
}
