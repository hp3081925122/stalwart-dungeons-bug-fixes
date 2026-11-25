package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.entity.SdEntityLogicHelper;
import net.mcreator.stalwartdungeons.procedures.ShulkerCannonItemRightclickedProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复潜影加农炮物品右键放置过程调用 Minecraft.getInstance() 崩溃及篡改 ItemStack.EMPTY 单例的严重 Bug。
 */
@Mixin(value = ShulkerCannonItemRightclickedProcedure.class, remap = false)
public abstract class ShulkerCannonItemRightClickProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeShulkerCannonRightClick(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixBossAndMobProceduresEnabled()) {
            return;
        }
        SdEntityLogicHelper.handleShulkerCannonItemRightClick(world, x, y, z, entity, itemstack);
        ci.cancel();
    }
}
