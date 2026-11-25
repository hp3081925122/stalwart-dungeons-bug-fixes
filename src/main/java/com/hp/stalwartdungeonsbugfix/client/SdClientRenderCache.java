package com.hp.stalwartdungeonsbugfix.client;

import net.mcreator.stalwartdungeons.client.model.Modelchorundum_helmet;
import net.mcreator.stalwartdungeons.client.model.Modeltungsten_helmet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;

/**
 * 客户端渲染资源缓存：
 * 1. 按 EntityModelSet 弱引用缓存 3 种自定义头盔模型（支持 F3+T 资源重载后自动重建），消除每帧 bakeLayer 与 8 个 ModelPart 分配；
 * 2. 缓存全部 11 个实体渲染器的贴图 ResourceLocation 与庇护者发光层 RenderType，消除每帧字符串解析与对象分配。
 */
public final class SdClientRenderCache {
    public static final ResourceLocation TEX_AWFUL_GHAST = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "textures/entities/awful_ghast.png");
    public static final ResourceLocation TEX_GIDDY_BLAZE = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "textures/entities/giddy_blaze.png");
    public static final ResourceLocation TEX_INCOMPLETE_WITHER = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "textures/entities/incomplete_wither.png");
    public static final ResourceLocation TEX_NETHER_KEEPER = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "textures/entities/nether_keeper.png");
    public static final ResourceLocation TEX_NETHER_WART_COCOON = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "textures/entities/nether_wart_cocoon.png");
    public static final ResourceLocation TEX_PROPULK = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "textures/entities/propulk.png");
    public static final ResourceLocation TEX_REINFORCED_BLAZE = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "textures/entities/renforced_blaze.png");
    public static final ResourceLocation TEX_SHELTERER = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "textures/entities/shelterer.png");
    public static final ResourceLocation TEX_SHELTERER_GLOW = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "textures/entities/shelterer_glow.png");
    public static final ResourceLocation TEX_SHULKER_CANNON = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "textures/entities/shulker_cannon.png");
    public static final ResourceLocation TEX_SPORE_PROJECTILE = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "textures/entities/void.png");

    public static final RenderType RENDER_TYPE_SHELTERER_EYES = RenderType.eyes(TEX_SHELTERER_GLOW);

    private static final Map<String, ResourceLocation> RENDERER_TEXTURES = Map.ofEntries(
            Map.entry("AwfulGhastRenderer", TEX_AWFUL_GHAST),
            Map.entry("GiddyBlazeRenderer", TEX_GIDDY_BLAZE),
            Map.entry("IncompleteWitherRenderer", TEX_INCOMPLETE_WITHER),
            Map.entry("NetherKeeperRenderer", TEX_NETHER_KEEPER),
            Map.entry("NetherWartCocoonRenderer", TEX_NETHER_WART_COCOON),
            Map.entry("PropulkRenderer", TEX_PROPULK),
            Map.entry("ReinforcedBlazeRenderer", TEX_REINFORCED_BLAZE),
            Map.entry("SheltererRenderer", TEX_SHELTERER),
            Map.entry("SheltererWithoutArmorRenderer", TEX_SHELTERER),
            Map.entry("ShulkerCannonRenderer", TEX_SHULKER_CANNON),
            Map.entry("SporeProjectileRenderer", TEX_SPORE_PROJECTILE)
    );

    private static WeakReference<EntityModelSet> cachedModelSetRef = new WeakReference<>(null);
    private static HumanoidModel<?> cachedChorundumHelmetModel;
    private static HumanoidModel<?> cachedTungstenHelmetModel;

    private SdClientRenderCache() {
    }

    public static ResourceLocation getCachedRendererTexture(Object renderer) {
        return RENDERER_TEXTURES.get(renderer.getClass().getSimpleName());
    }

    private static void validateModelSet(EntityModelSet current) {
        if (cachedModelSetRef.get() != current) {
            cachedModelSetRef = new WeakReference<>(current);
            cachedChorundumHelmetModel = null;
            cachedTungstenHelmetModel = null;
        }
    }

    private static HumanoidModel<?> buildHeadOnlyModel(ModelPart headPart) {
        ModelPart empty = new ModelPart(Collections.emptyList(), Collections.emptyMap());
        return new HumanoidModel<>(new ModelPart(
                Collections.emptyList(),
                Map.of(
                        "head", headPart,
                        "hat", empty,
                        "body", empty,
                        "right_arm", empty,
                        "left_arm", empty,
                        "right_leg", empty,
                        "left_leg", empty
                )
        ));
    }

    public static HumanoidModel<?> getChorundumHelmetModel(LivingEntity living, HumanoidModel<?> defaultModel) {
        EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
        validateModelSet(modelSet);
        if (cachedChorundumHelmetModel == null) {
            Modelchorundum_helmet<?> baked = new Modelchorundum_helmet<>(modelSet.bakeLayer(Modelchorundum_helmet.LAYER_LOCATION));
            cachedChorundumHelmetModel = buildHeadOnlyModel(baked.head);
        }
        syncPose(cachedChorundumHelmetModel, living, defaultModel);
        return cachedChorundumHelmetModel;
    }

    public static HumanoidModel<?> getTungstenHelmetModel(LivingEntity living, HumanoidModel<?> defaultModel) {
        EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
        validateModelSet(modelSet);
        if (cachedTungstenHelmetModel == null) {
            Modeltungsten_helmet<?> baked = new Modeltungsten_helmet<>(modelSet.bakeLayer(Modeltungsten_helmet.LAYER_LOCATION));
            cachedTungstenHelmetModel = buildHeadOnlyModel(baked.head);
        }
        syncPose(cachedTungstenHelmetModel, living, defaultModel);
        return cachedTungstenHelmetModel;
    }

    private static void syncPose(HumanoidModel<?> model, LivingEntity living, HumanoidModel<?> defaultModel) {
        model.crouching = living.isShiftKeyDown();
        model.riding = defaultModel.riding;
        model.young = living.isBaby();
    }
}
