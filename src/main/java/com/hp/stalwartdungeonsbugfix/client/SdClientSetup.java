package com.hp.stalwartdungeonsbugfix.client;

import com.hp.stalwartdungeonsbugfix.Stalwartdungeonsbugfix;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * 客户端初始化：为三面自定义盾牌注册原版同款 blocking 物品模型谓词，修复第一人称/第三人称举盾不抬起的表现。
 */
@Mod.EventBusSubscriber(modid = Stalwartdungeonsbugfix.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class SdClientSetup {
    private static final ResourceLocation BLOCKING_PROPERTY = ResourceLocation.withDefaultNamespace("blocking");

    private SdClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerShieldBlockingProperty(StalwartDungeonsModItems.TUNGSTEN_SHIELD.get());
            registerShieldBlockingProperty(StalwartDungeonsModItems.WARTED_TUNGSTEN_SHIELD.get());
            registerShieldBlockingProperty(StalwartDungeonsModItems.CHORUNDUM_SHIELD.get());
        });
    }

    private static void registerShieldBlockingProperty(Item shield) {
        ItemProperties.register(shield, BLOCKING_PROPERTY, (stack, level, entity, seed) ->
                entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F);
    }
}
