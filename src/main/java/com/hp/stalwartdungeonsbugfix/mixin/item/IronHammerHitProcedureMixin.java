package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.item.SdItemLogicHelper;
import net.mcreator.stalwartdungeons.procedures.IronHammerLivingEntityIsHitWithToolProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复战锤雷击附魔：判空 LightningBolt.create 并设置施法者 ServerPlayer。
 */
@Mixin(value = IronHammerLivingEntityIsHitWithToolProcedure.class, remap = false)
public abstract class IronHammerHitProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeIronHammerLightning(LevelAccessor world, double x, double y, double z, Entity sourceentity, ItemStack itemstack, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixItemProceduresAndMechanicsEnabled()) {
            return;
        }
        SdItemLogicHelper.handleHammerThunderStrike(world, x, y, z, null, sourceentity, itemstack, false);
        ci.cancel();
    }
}
