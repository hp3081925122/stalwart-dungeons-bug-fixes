package com.hp.stalwartdungeonsbugfix.mixin.client;

import com.hp.stalwartdungeonsbugfix.client.SdClientRenderCache;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.client.renderer.AwfulGhastRenderer;
import net.mcreator.stalwartdungeons.client.renderer.GiddyBlazeRenderer;
import net.mcreator.stalwartdungeons.client.renderer.IncompleteWitherRenderer;
import net.mcreator.stalwartdungeons.client.renderer.NetherKeeperRenderer;
import net.mcreator.stalwartdungeons.client.renderer.NetherWartCocoonRenderer;
import net.mcreator.stalwartdungeons.client.renderer.PropulkRenderer;
import net.mcreator.stalwartdungeons.client.renderer.ReinforcedBlazeRenderer;
import net.mcreator.stalwartdungeons.client.renderer.SheltererRenderer;
import net.mcreator.stalwartdungeons.client.renderer.SheltererWithoutArmorRenderer;
import net.mcreator.stalwartdungeons.client.renderer.ShulkerCannonRenderer;
import net.mcreator.stalwartdungeons.client.renderer.SporeProjectileRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 缓存 11 个实体渲染器的贴图 ResourceLocation，消除每帧通过 new ResourceLocation 解析字符串产生的临时对象分配。
 */
@Mixin(value = {
        AwfulGhastRenderer.class,
        GiddyBlazeRenderer.class,
        IncompleteWitherRenderer.class,
        NetherKeeperRenderer.class,
        NetherWartCocoonRenderer.class,
        PropulkRenderer.class,
        ReinforcedBlazeRenderer.class,
        SheltererRenderer.class,
        SheltererWithoutArmorRenderer.class,
        ShulkerCannonRenderer.class,
        SporeProjectileRenderer.class
}, remap = false)
public abstract class EntityRendererTextureCacheMixin {
    @Inject(method = "getTextureLocation", at = @At("HEAD"), cancellable = true, remap = false)
    private void sd$returnCachedTexture(CallbackInfoReturnable<ResourceLocation> cir) {
        if (!SdBugfixConfig.isCacheEntityRendererTexturesEnabled()) {
            return;
        }
        ResourceLocation cached = SdClientRenderCache.getCachedRendererTexture(this);
        if (cached != null) {
            cir.setReturnValue(cached);
        }
    }
}
