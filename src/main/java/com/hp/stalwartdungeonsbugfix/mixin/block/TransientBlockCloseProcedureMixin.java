package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.block.SdBlockLogicHelper;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.procedures.TransientBlockCloseUpdateTickProcedure;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复关闭态瞬态方块传播过程：仅服务端执行并移除无意义的 0 音量音效包。
 */
@Mixin(value = TransientBlockCloseUpdateTickProcedure.class, remap = false)
public abstract class TransientBlockCloseProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeTransientCloseTick(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        if (!SdBugfixConfig.isOptimizeTransientBlockTickingEnabled()) {
            return;
        }
        SdBlockLogicHelper.handleTransientCloseTick(world, x, y, z);
        ci.cancel();
    }
}
