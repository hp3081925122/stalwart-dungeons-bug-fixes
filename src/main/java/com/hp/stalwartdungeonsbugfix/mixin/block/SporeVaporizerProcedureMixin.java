package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.block.SdBlockLogicHelper;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.procedures.SporeVaporizerUpdateTickProcedure;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复孢子喷发器：消除每刻 4 次匿名类分配，排除旁观者触发并限制周围孢子投射物数量上限。
 */
@Mixin(value = SporeVaporizerUpdateTickProcedure.class, remap = false)
public abstract class SporeVaporizerProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeSporeVaporizerTick(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixTrapBlocksAndTeleporterEnabled()) {
            return;
        }
        SdBlockLogicHelper.handleSporeVaporizerTick(world, x, y, z);
        ci.cancel();
    }
}
