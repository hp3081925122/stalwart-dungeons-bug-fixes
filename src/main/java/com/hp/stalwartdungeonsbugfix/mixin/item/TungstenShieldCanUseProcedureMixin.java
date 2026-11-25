package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.procedures.TungstenShieldCanUseRangedItemProcedure;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复三面盾牌松开右键时扫描全服熔炼配方的问题：
 * 原作者用“盾牌能否熔炼”模拟恒为 false 以禁止 MCreator 远程物品开火，但每次松手都会遍历全部熔炼配方，
 * 且若整合包添加了盾牌回炉配方还会导致松手损耐久并射出隐形箭。直接返回 false 彻底根除。
 */
@Mixin(value = TungstenShieldCanUseRangedItemProcedure.class, remap = false)
public abstract class TungstenShieldCanUseProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$neverShootDummyShieldArrow(LevelAccessor world, ItemStack itemstack, CallbackInfoReturnable<Boolean> cir) {
        if (!SdBugfixConfig.isFixItemProceduresAndMechanicsEnabled()) {
            return;
        }
        cir.setReturnValue(false);
    }
}
