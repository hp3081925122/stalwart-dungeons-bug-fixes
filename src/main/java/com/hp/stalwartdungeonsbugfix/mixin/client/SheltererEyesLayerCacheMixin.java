package com.hp.stalwartdungeonsbugfix.mixin.client;

import com.hp.stalwartdungeonsbugfix.client.SdClientRenderCache;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 缓存庇护者发光层的 RenderType，消除每帧重复 new ResourceLocation 与 RenderType.eyes 构造。
 */
@Mixin(targets = "net.mcreator.stalwartdungeons.client.renderer.SheltererRenderer$1")
public abstract class SheltererEyesLayerCacheMixin {
    @Inject(method = "renderType", at = @At("HEAD"), cancellable = true)
    private void sd$returnCachedEyesRenderType(CallbackInfoReturnable<RenderType> cir) {
        if (!SdBugfixConfig.isCacheEntityRendererTexturesEnabled()) {
            return;
        }
        cir.setReturnValue(SdClientRenderCache.RENDER_TYPE_SHELTERER_EYES);
    }
}
