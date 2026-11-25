package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.block.SdBlockLogicHelper;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.procedures.TransientBlockOpenUpdateTickProcedure;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复开启态瞬态方块倒计时关闭：修正 == 5.0 泄漏，消除每刻 sendBlockUpdated 与 0 音量音效包。
 */
@Mixin(value = TransientBlockOpenUpdateTickProcedure.class, remap = false)
public abstract class TransientBlockOpenProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeTransientOpenTick(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        if (!SdBugfixConfig.isOptimizeTransientBlockTickingEnabled()) {
            return;
        }
        SdBlockLogicHelper.handleTransientOpenTick(world, x, y, z);
        ci.cancel();
    }
}
