package com.hp.stalwartdungeonsbugfix.client;

import com.hp.stalwartdungeonsbugfix.Stalwartdungeonsbugfix;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Map;

/**
 * 客户端物品 Tooltip 拼写修正：
 * 修正原模组硬编码在 Java appendHoverText 里的英文拼写错误（Brun/Imunise/ennemy），无需修改或侵入语言包。
 */
@Mod.EventBusSubscriber(modid = Stalwartdungeonsbugfix.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SdClientTooltipHandler {
    private static final Map<String, String> EXACT_FIXES = Map.of(
            "§9 Brun and Smelt the Block", "§9 Burn and Smelt the Block",
            "§9 Imunise you to Burning and Wither Effect", "§9 Immunize you to Burning and Wither Effect",
            "§9 Imunise you to Spore and Wither Effect", "§9 Immunize you to Spore and Wither Effect",
            "§9 Imunise you to Levitation Effect", "§9 Immunize you to Levitation Effect",
            "§9 Clear all effect of ennemy", "§9 Clear all effects of enemy"
    );

    private SdClientTooltipHandler() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!SdBugfixConfig.isFixItemTooltipTyposEnabled()) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (key == null || !"stalwart_dungeons".equals(key.getNamespace())) {
            return;
        }
        List<Component> lines = event.getToolTip();
        for (int i = 0; i < lines.size(); i++) {
            String text = lines.get(i).getString();
            String fixed = EXACT_FIXES.get(text);
            if (fixed != null) {
                lines.set(i, Component.literal(fixed));
            }
        }
    }
}
