package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.block.SdBlockLogicHelper;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.procedures.ObsidianStrikerOnBlockRightClickedProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复黑曜石击发器：仅服务端运行，正确校验 TNT 冷却且创造模式不消耗 TNT。
 */
@Mixin(value = ObsidianStrikerOnBlockRightClickedProcedure.class, remap = false)
public abstract class ObsidianStrikerProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeObsidianStriker(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixBossAndMobProceduresEnabled()) {
            return;
        }
        SdBlockLogicHelper.handleObsidianStrikerUse(world, x, y, z, entity);
        ci.cancel();
    }
}
