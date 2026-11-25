package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.block.SdBlockLogicHelper;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.procedures.TransientBlockCloseRedstoneOnProcedure;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复关闭态瞬态方块红石/右键开启过程：仅服务端修改方块与发声，避免客户端幽灵切换。
 */
@Mixin(value = TransientBlockCloseRedstoneOnProcedure.class, remap = false)
public abstract class TransientBlockCloseRedstoneProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeTransientRedstoneOn(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        if (!SdBugfixConfig.isOptimizeTransientBlockTickingEnabled()) {
            return;
        }
        SdBlockLogicHelper.handleTransientCloseRedstoneOrUse(world, x, y, z);
        ci.cancel();
    }
}
