package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.procedures.NetherHammerBlockDestroyedWithToolProcedure;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 停用失效且存在重复掉落隐患的旧版 mineBlock 后置熔炼过程，由 BlockNetherHammerAutoSmeltMixin 统一在掉落计算阶段接管。
 */
@Mixin(value = NetherHammerBlockDestroyedWithToolProcedure.class, remap = false)
public abstract class NetherHammerBrokenProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$disableBrokenPostMineSmelt(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        if (SdBugfixConfig.isFixItemProceduresAndMechanicsEnabled()) {
            ci.cancel();
        }
    }
}
