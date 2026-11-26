package com.hp.stalwartdungeonsbugfix.mixin.item;

import java.util.List;
import net.mcreator.stalwartdungeons.item.AwfulDaggerItem;
import net.mcreator.stalwartdungeons.item.AwfulGunItem;
import net.mcreator.stalwartdungeons.item.ChorundumArmorItem;
import net.mcreator.stalwartdungeons.item.NetherHammerItem;
import net.mcreator.stalwartdungeons.item.ShulkerCannonItemItem;
import net.mcreator.stalwartdungeons.item.TungstenItem;
import net.mcreator.stalwartdungeons.item.WartedTungstenItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 将坚固地牢全部 16 个硬编码英文 Tooltip 物品/盔甲改为标准翻译键，便于多语言翻译并同步修正英文拼写错误。
 */
@Mixin({
        AwfulDaggerItem.class,
        AwfulGunItem.class,
        NetherHammerItem.class,
        ShulkerCannonItemItem.class,
        ChorundumArmorItem.Helmet.class,
        ChorundumArmorItem.Chestplate.class,
        ChorundumArmorItem.Leggings.class,
        ChorundumArmorItem.Boots.class,
        TungstenItem.Helmet.class,
        TungstenItem.Chestplate.class,
        TungstenItem.Leggings.class,
        TungstenItem.Boots.class,
        WartedTungstenItem.Helmet.class,
        WartedTungstenItem.Chestplate.class,
        WartedTungstenItem.Leggings.class,
        WartedTungstenItem.Boots.class
})
public abstract class HardcodedItemTooltipsMixin extends Item {
    protected HardcodedItemTooltipsMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
    private void sd$localizedItemTooltip(
            ItemStack stack, Level world, List<Component> list, TooltipFlag flag, CallbackInfo ci
    ) {
        super.appendHoverText(stack, world, list, flag);
        Object self = this;
        if (self instanceof AwfulDaggerItem) {
            list.add(Component.translatable("tooltip.stalwart_dungeons.when_hit").withStyle(ChatFormatting.GRAY));
            list.add(Component.literal(" ").append(Component.translatable("tooltip.stalwart_dungeons.awful_dagger")).withStyle(ChatFormatting.BLUE));
        } else if (self instanceof AwfulGunItem) {
            list.add(Component.translatable("tooltip.stalwart_dungeons.awful_gun").withStyle(ChatFormatting.GRAY));
        } else if (self instanceof NetherHammerItem) {
            list.add(Component.translatable("tooltip.stalwart_dungeons.when_used").withStyle(ChatFormatting.GRAY));
            list.add(Component.literal(" ").append(Component.translatable("tooltip.stalwart_dungeons.nether_hammer")).withStyle(ChatFormatting.BLUE));
        } else if (self instanceof ShulkerCannonItemItem) {
            list.add(Component.translatable("tooltip.stalwart_dungeons.shulker_cannon").withStyle(ChatFormatting.BLUE));
        } else if (self instanceof ChorundumArmorItem) {
            list.add(Component.translatable("tooltip.stalwart_dungeons.full_set").withStyle(ChatFormatting.GRAY));
            list.add(Component.literal(" ").append(Component.translatable("tooltip.stalwart_dungeons.chorundum_armor")).withStyle(ChatFormatting.BLUE));
        } else if (self instanceof TungstenItem) {
            list.add(Component.translatable("tooltip.stalwart_dungeons.full_set").withStyle(ChatFormatting.GRAY));
            list.add(Component.literal(" ").append(Component.translatable("tooltip.stalwart_dungeons.tungsten_armor")).withStyle(ChatFormatting.BLUE));
        } else if (self instanceof WartedTungstenItem) {
            list.add(Component.translatable("tooltip.stalwart_dungeons.full_set").withStyle(ChatFormatting.GRAY));
            list.add(Component.literal(" ").append(Component.translatable("tooltip.stalwart_dungeons.warted_tungsten_armor")).withStyle(ChatFormatting.BLUE));
        }
        ci.cancel();
    }
}
