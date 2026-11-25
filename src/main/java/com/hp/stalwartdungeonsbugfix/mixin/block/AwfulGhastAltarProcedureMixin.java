package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.block.SdBlockLogicHelper;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModEntities;
import net.mcreator.stalwartdungeons.procedures.AwfulGhastAltarOnBlockRightClickedProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复可怕恶魂祭坛：仅服务端生成 Boss 与消耗下界之星，创造模式不扣除。
 */
@Mixin(value = AwfulGhastAltarOnBlockRightClickedProcedure.class, remap = false)
public abstract class AwfulGhastAltarProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeAwfulGhastAltar(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixBossAndMobProceduresEnabled()) {
            return;
        }
        SdBlockLogicHelper.handleBossAltarUse(world, x, y, z, entity, StalwartDungeonsModEntities.AWFUL_GHAST.get(), 1.0, false);
        ci.cancel();
    }
}
