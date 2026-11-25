package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修正错误注册的实体刷怪分类：
 * - propulk（敌对推进虫）从 CREATURE 修正为 MONSTER，防止抢占友好生物上限且允许和平模式正常清除；
 * - spore_projectile（孢子投射物）从 MONSTER 修正为 MISC，防止占用敌对刷怪上限。
 */
@Mixin(EntityType.class)
public abstract class EntityTypeCategoryMixin {
    @Inject(method = "getCategory", at = @At("HEAD"), cancellable = true)
    private void sd$fixEntityMobCategories(CallbackInfoReturnable<MobCategory> cir) {
        if (!SdBugfixConfig.isFixEntityMobCategoriesEnabled()) {
            return;
        }
        Object self = this;
        if (StalwartDungeonsModEntities.PROPULK.isPresent() && self == StalwartDungeonsModEntities.PROPULK.get()) {
            cir.setReturnValue(MobCategory.MONSTER);
        } else if (StalwartDungeonsModEntities.SPORE_PROJECTILE.isPresent() && self == StalwartDungeonsModEntities.SPORE_PROJECTILE.get()) {
            cir.setReturnValue(MobCategory.MISC);
        }
    }
}
