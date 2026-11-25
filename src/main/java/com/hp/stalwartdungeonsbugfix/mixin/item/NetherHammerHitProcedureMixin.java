package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.item.SdItemLogicHelper;
import net.mcreator.stalwartdungeons.procedures.NetherHammerLivingEntityIsHitWithToolProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复下界之锤命中生物的雷击附魔：判空 LightningBolt.create 并设置施法者 ServerPlayer，同时保留 12 秒引燃效果。
 */
@Mixin(value = NetherHammerLivingEntityIsHitWithToolProcedure.class, remap = false)
public abstract class NetherHammerHitProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeNetherHammerLightning(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity, ItemStack itemstack, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixItemProceduresAndMechanicsEnabled()) {
            return;
        }
        SdItemLogicHelper.handleHammerThunderStrike(world, x, y, z, entity, sourceentity, itemstack, true);
        ci.cancel();
    }
}
