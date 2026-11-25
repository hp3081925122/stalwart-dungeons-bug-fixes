package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.item.SdItemLogicHelper;
import net.mcreator.stalwartdungeons.enchantment.ThunderStrikeEnchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 显式挂钩 Enchantment.canEnchant，防止铁砧将雷击附魔打到普通剑等不会触发该附魔的物品上。
 */
@Mixin(Enchantment.class)
public abstract class ThunderStrikeCanEnchantMixin {
    @Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
    private void sd$restrictThunderStrikeAnvil(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ThunderStrikeEnchantment && SdBugfixConfig.isFixItemProceduresAndMechanicsEnabled()) {
            cir.setReturnValue(SdItemLogicHelper.isThunderStrikeHammer(stack));
        }
    }
}
