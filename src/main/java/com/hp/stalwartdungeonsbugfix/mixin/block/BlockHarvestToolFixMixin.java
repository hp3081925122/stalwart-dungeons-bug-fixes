package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.block.AwfulGhastAltarBlock;
import net.mcreator.stalwartdungeons.block.ChiseledEndStoneBricksBlock;
import net.mcreator.stalwartdungeons.block.ChorundumOreBlock;
import net.mcreator.stalwartdungeons.block.CrackedEndstoneBricksBlock;
import net.mcreator.stalwartdungeons.block.IgniterBlock;
import net.mcreator.stalwartdungeons.block.NetherKeeperAltarBlock;
import net.mcreator.stalwartdungeons.block.ObsidianStrikerBlock;
import net.mcreator.stalwartdungeons.block.PropulserBlockBlock;
import net.mcreator.stalwartdungeons.block.PurpurBricksBlock;
import net.mcreator.stalwartdungeons.block.PurpurBricksSlabBlock;
import net.mcreator.stalwartdungeons.block.PurpurBricksStairsBlock;
import net.mcreator.stalwartdungeons.block.PurpurBricksWallBlock;
import net.mcreator.stalwartdungeons.block.PusherBlock;
import net.mcreator.stalwartdungeons.block.SheltererAltarBlock;
import net.mcreator.stalwartdungeons.block.SoulBricksBlock;
import net.mcreator.stalwartdungeons.block.SoulBricksSlabBlock;
import net.mcreator.stalwartdungeons.block.SoulBricksStairsBlock;
import net.mcreator.stalwartdungeons.block.SoulBricksWallBlock;
import net.mcreator.stalwartdungeons.block.SoulMagmaBricksBlock;
import net.mcreator.stalwartdungeons.block.SoulTilesBlock;
import net.mcreator.stalwartdungeons.block.SoulTilesSlabBlock;
import net.mcreator.stalwartdungeons.block.TeleporterBlockBlock;
import net.mcreator.stalwartdungeons.block.TungstenOreBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复全部 23 个方块硬编码 instanceof PickaxeItem 导致无法被匠魂、Tetra 等标签化模块化工具正常挖掘的问题。
 * 统一回退到原版/Forge 标准标签挖掘判定（配合数据包补全的 mineable/pickaxe 与 needs_*_tool 标签）。
 */
@Mixin(value = {
        AwfulGhastAltarBlock.class,
        ChiseledEndStoneBricksBlock.class,
        ChorundumOreBlock.class,
        CrackedEndstoneBricksBlock.class,
        IgniterBlock.class,
        NetherKeeperAltarBlock.class,
        ObsidianStrikerBlock.class,
        PropulserBlockBlock.class,
        PurpurBricksBlock.class,
        PurpurBricksSlabBlock.class,
        PurpurBricksStairsBlock.class,
        PurpurBricksWallBlock.class,
        PusherBlock.class,
        SheltererAltarBlock.class,
        SoulBricksBlock.class,
        SoulBricksSlabBlock.class,
        SoulBricksStairsBlock.class,
        SoulBricksWallBlock.class,
        SoulMagmaBricksBlock.class,
        SoulTilesBlock.class,
        SoulTilesSlabBlock.class,
        TeleporterBlockBlock.class,
        TungstenOreBlock.class
}, remap = false)
public abstract class BlockHarvestToolFixMixin extends Block {
    private BlockHarvestToolFixMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "canHarvestBlock", at = @At("HEAD"), cancellable = true)
    private void sd$useStandardTagHarvestCheck(
            BlockState state,
            BlockGetter world,
            BlockPos pos,
            Player player,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!SdBugfixConfig.isFixHardcodedBlockHarvestEnabled()) {
            return;
        }
        cir.setReturnValue(super.canHarvestBlock(state, world, pos, player));
    }
}
