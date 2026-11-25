package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.item.SdItemLogicHelper;
import net.mcreator.stalwartdungeons.enchantment.ThunderStrikeEnchantment;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复雷击附魔（Thunder Strike）在附魔台每次校验重复分配数组与 Ingredient 的问题。
 */
@Mixin(value = ThunderStrikeEnchantment.class, remap = false)
public abstract class ThunderStrikeEnchantmentMixin {
    @Inject(method = "canApplyAtEnchantingTable", at = @At("HEAD"), cancellable = true)
    private void sd$cachedCanApply(ItemStack itemstack, CallbackInfoReturnable<Boolean> cir) {
        if (!SdBugfixConfig.isFixItemProceduresAndMechanicsEnabled()) {
            return;
        }
        cir.setReturnValue(SdItemLogicHelper.isThunderStrikeHammer(itemstack));
    }
}
