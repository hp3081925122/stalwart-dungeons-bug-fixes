package com.hp.stalwartdungeonsbugfix.mixin.entity;

import net.mcreator.stalwartdungeons.procedures.ShulkerCannonEntityDiesProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 移除潜影加农炮死亡过程内的硬编码刷物，交由标准实体战利品表（entities/shulker_cannon.json）掉落，避免重复掉落。
 */
@Mixin(value = ShulkerCannonEntityDiesProcedure.class, remap = false)
public abstract class ShulkerCannonDiesProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$delegateCannonDeathDropToLootTable(
            LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci
    ) {
        if (entity != null && !entity.level().isClientSide()) {
            entity.discard();
        }
        ci.cancel();
    }
}
