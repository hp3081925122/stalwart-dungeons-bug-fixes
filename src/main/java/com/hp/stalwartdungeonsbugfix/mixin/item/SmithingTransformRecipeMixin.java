package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复 1.20.1 锻造台空模板配方被 Forge 判定为不完整（isIncomplete=true）的问题：
 * 疣化钨护甲与疣化钨盾的锻造台升级配方本身不需要模板物品（template=[]），
 * 但原版 SmithingTransformRecipe#isIncomplete 会把空 template 视为缺失原料导致 JEI/配方书异常；
 * 此处仅针对 stalwart_dungeons 命名空间的空模板配方放行 template 检查，仍严格校验 base 与 addition。
 */
@Mixin(SmithingTransformRecipe.class)
public abstract class SmithingTransformRecipeMixin {
    @Shadow
    @Final
    private ResourceLocation id;

    @Shadow
    @Final
    Ingredient template;

    @Shadow
    @Final
    Ingredient base;

    @Shadow
    @Final
    Ingredient addition;

    @Inject(method = "isIncomplete", at = @At("HEAD"), cancellable = true)
    private void sd$allowTemplateFreeStalwartSmithing(CallbackInfoReturnable<Boolean> cir) {
        if (!SdBugfixConfig.isFixSmithingRecipesEnabled()) {
            return;
        }
        if (this.id != null && "stalwart_dungeons".equals(this.id.getNamespace()) && this.template.isEmpty()) {
            cir.setReturnValue(ForgeHooks.hasNoElements(this.base) || ForgeHooks.hasNoElements(this.addition));
        }
    }
}
