package com.hp.stalwartdungeonsbugfix.mixin.entity;

import net.mcreator.stalwartdungeons.init.StalwartDungeonsModEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修正坚固地牢全部 21 种实体注册时误以方块单位填入的 64 区块（1024 格）追踪半径：
 * 弹射物收敛为 6 区块（96 格）、常规实体 8 区块（128 格）、Boss 10 区块（160 格）。
 * 注意：register 在目标类静态初始化阶段执行，不依赖尚未加载的配置文件。
 */
@Mixin(value = StalwartDungeonsModEntities.class, remap = false)
public abstract class StalwartEntitiesTrackingMixin {
    @Inject(method = "register", at = @At("HEAD"), remap = false)
    private static <T extends Entity> void sd$clampTrackingRange(
            String registryname,
            EntityType.Builder<T> entityTypeBuilder,
            CallbackInfoReturnable<RegistryObject<EntityType<T>>> cir
    ) {
        if (registryname == null || entityTypeBuilder == null) {
            return;
        }
        int clamped = sd$resolveTrackingRange(registryname);
        entityTypeBuilder.clientTrackingRange(clamped);
        entityTypeBuilder.setTrackingRange(clamped);
    }

    @Unique
    private static int sd$resolveTrackingRange(String registryname) {
        if (registryname.startsWith("projectile_")) {
            return 6;
        }
        return switch (registryname) {
            case "awful_ghast", "nether_keeper", "shelterer", "shelterer_without_armor" -> 10;
            default -> 8;
        };
    }
}
