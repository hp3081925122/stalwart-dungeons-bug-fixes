package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.item.SdItemLogicHelper;
import net.mcreator.stalwartdungeons.item.TeleporterPearlItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * 修复传送珍珠悬停提示调用 itemstack.getOrCreateTag() 导致的空 {} NBT 污染与堆叠失效问题。
 */
@Mixin(TeleporterPearlItem.class)
public abstract class TeleporterPearlItemMixin extends Item {
    private TeleporterPearlItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "appendHoverText", at = @At("HEAD"), cancellable = true)
    private void sd$readOnlyHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixTrapBlocksAndTeleporterEnabled()) {
            return;
        }
        super.appendHoverText(itemstack, world, list, flag);
        SdItemLogicHelper.appendTeleporterPearlTooltip(itemstack, list);
        ci.cancel();
    }
}
