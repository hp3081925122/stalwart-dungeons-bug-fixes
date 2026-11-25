package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.block.SdBlockLogicHelper;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.procedures.SoulMagmaBricksEntityWalksOnTheBlockProcedure;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复灵魂岩浆砖踩踏扣血：仅服务端生效，并遵循原版岩浆块的潜行与冰霜行者免疫机制。
 */
@Mixin(value = SoulMagmaBricksEntityWalksOnTheBlockProcedure.class, remap = false)
public abstract class SoulMagmaBricksProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeSoulMagmaStep(Entity entity, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixTrapBlocksAndTeleporterEnabled()) {
            return;
        }
        SdBlockLogicHelper.handleSoulMagmaStep(entity);
        ci.cancel();
    }
}
