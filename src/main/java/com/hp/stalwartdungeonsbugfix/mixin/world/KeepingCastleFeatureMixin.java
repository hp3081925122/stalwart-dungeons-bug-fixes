package com.hp.stalwartdungeonsbugfix.mixin.world;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.world.SdWorldgenHelper;
import net.mcreator.stalwartdungeons.world.features.KeepingCastleFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复守望城堡世界生成：网格最小间距、安全下界高度与基岩顶/底保护、线程安全模板获取。
 */
@Mixin(KeepingCastleFeature.class)
public abstract class KeepingCastleFeatureMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void sd$safePlaceKeepingCastle(FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        if (!SdBugfixConfig.isFixDungeonWorldgenEnabled()) {
            return;
        }
        cir.setReturnValue(SdWorldgenHelper.placeNetherDungeonAnchor(context, false));
    }
}
