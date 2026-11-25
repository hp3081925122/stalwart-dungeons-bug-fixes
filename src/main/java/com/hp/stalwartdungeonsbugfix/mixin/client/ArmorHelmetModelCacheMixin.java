package com.hp.stalwartdungeonsbugfix.mixin.client;

import com.hp.stalwartdungeonsbugfix.client.SdClientRenderCache;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.item.ChorundumArmorItem;
import net.mcreator.stalwartdungeons.item.TungstenItem;
import net.mcreator.stalwartdungeons.item.WartedTungstenItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/**
 * 修复钨、疣化钨与紫颂锭头盔每帧重复 bakeLayer 与构造 8 个 ModelPart 造成的严重客户端掉帧与 GC 抖动。
 */
@Mixin(value = {
        ChorundumArmorItem.Helmet.class,
        TungstenItem.Helmet.class,
        WartedTungstenItem.Helmet.class
}, remap = false)
public abstract class ArmorHelmetModelCacheMixin {
    @Inject(method = "initializeClient", at = @At("HEAD"), cancellable = true)
    private void sd$registerCachedHelmetModel(Consumer<IClientItemExtensions> consumer, CallbackInfo ci) {
        if (!SdBugfixConfig.isCacheArmorHelmetModelsEnabled()) {
            return;
        }
        final boolean isChorundum = (Object) this instanceof ChorundumArmorItem.Helmet;
        consumer.accept(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> defaultModel) {
                return isChorundum
                        ? SdClientRenderCache.getChorundumHelmetModel(living, defaultModel)
                        : SdClientRenderCache.getTungstenHelmetModel(living, defaultModel);
            }
        });
        ci.cancel();
    }
}
