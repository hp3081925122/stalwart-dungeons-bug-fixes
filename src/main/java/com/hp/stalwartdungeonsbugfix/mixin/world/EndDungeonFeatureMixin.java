package com.hp.stalwartdungeonsbugfix.mixin.world;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.world.SdWorldgenHelper;
import net.mcreator.stalwartdungeons.world.features.EndDungeonFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复末地地牢世界生成：跳过 the_void 与末地虚空坐标、保护末地主岛、网格最小间距、支持全部四方向旋转。
 */
@Mixin(EndDungeonFeature.class)
public abstract class EndDungeonFeatureMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void sd$safePlaceEndDungeon(FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        if (!SdBugfixConfig.isFixDungeonWorldgenEnabled()) {
            return;
        }
        cir.setReturnValue(SdWorldgenHelper.placeEndDungeonAnchor(context));
    }
}
