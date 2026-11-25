package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModItems;
import net.mcreator.stalwartdungeons.item.ChorundumShieldItem;
import net.mcreator.stalwartdungeons.item.TungstenShieldItem;
import net.mcreator.stalwartdungeons.item.WartedTungstenShieldItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.spongepowered.asm.mixin.Mixin;

/**
 * 修复钨盾、疣化钨盾与紫颂盾在 1.20.1 Forge 完全无法格挡的严重问题：
 * 原模组三面盾牌直接继承 Item 而非 ShieldItem，未覆写 Forge 的 canPerformAction(ToolActions.SHIELD_BLOCK)，
 * 导致 LivingEntity#isBlocking() 恒为 false，无法格挡伤害、弹反箭矢或触发破盾；
 * 同时补全铁砧修理材料（钨锭 / 紫颂锭）。
 */
@Mixin({TungstenShieldItem.class, WartedTungstenShieldItem.class, ChorundumShieldItem.class})
public abstract class CustomShieldBlockingMixin extends Item {
    protected CustomShieldBlockingMixin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        if (SdBugfixConfig.isFixCustomShieldsEnabled()) {
            return ToolActions.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
        }
        return super.canPerformAction(stack, toolAction);
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        if (SdBugfixConfig.isFixCustomShieldsEnabled()) {
            Object self = this;
            if (self instanceof ChorundumShieldItem) {
                return repair.is(StalwartDungeonsModItems.CHORUNDUM.get());
            }
            return repair.is(StalwartDungeonsModItems.TUNGSTEN_INGOT.get());
        }
        return super.isValidRepairItem(toRepair, repair);
    }
}
