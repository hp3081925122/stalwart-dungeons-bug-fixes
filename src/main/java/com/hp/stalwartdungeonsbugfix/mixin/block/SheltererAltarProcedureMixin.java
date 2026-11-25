package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.block.SdBlockLogicHelper;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModEntities;
import net.mcreator.stalwartdungeons.procedures.SheltererAltarOnBlockRightClickedProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复庇护者祭坛：仅服务端生成 Boss 与消耗下界之星，创造模式不扣除。
 */
@Mixin(value = SheltererAltarOnBlockRightClickedProcedure.class, remap = false)
public abstract class SheltererAltarProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeSheltererAltar(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixBossAndMobProceduresEnabled()) {
            return;
        }
        SdBlockLogicHelper.handleBossAltarUse(world, x, y, z, entity, StalwartDungeonsModEntities.SHELTERER.get(), 2.0, false);
        ci.cancel();
    }
}
