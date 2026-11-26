package com.hp.stalwartdungeonsbugfix.mixin.block;

import java.util.List;
import net.mcreator.stalwartdungeons.block.AwfulDungeonsSpawnBlock;
import net.mcreator.stalwartdungeons.block.AwfulGhastAltarBlock;
import net.mcreator.stalwartdungeons.block.ChiseledEndStoneBricksBlock;
import net.mcreator.stalwartdungeons.block.CrackedEndstoneBricksBlock;
import net.mcreator.stalwartdungeons.block.EnderGlobeSpawnBlock;
import net.mcreator.stalwartdungeons.block.GiddyStatueBlock;
import net.mcreator.stalwartdungeons.block.IgniterBlock;
import net.mcreator.stalwartdungeons.block.KeepingCastleSpawnBlock;
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
import net.mcreator.stalwartdungeons.block.SporeVaporizerBlock;
import net.mcreator.stalwartdungeons.block.TeleporterBlockBlock;
import net.mcreator.stalwartdungeons.block.TransientBlockCloseBlock;
import net.mcreator.stalwartdungeons.block.TransientBlockOpenBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 将全部 28 个原本依靠硬编码 getDrops() 掉落的坚固地牢方块改为纯数据驱动 JSON 战利品表（委派回 super.getDrops），
 * 使 JEI/JER/EMI Loot 能完整展示方块掉落，并支持数据包与 LootJS 自由覆盖。
 */
@Mixin({
        AwfulDungeonsSpawnBlock.class,
        AwfulGhastAltarBlock.class,
        ChiseledEndStoneBricksBlock.class,
        CrackedEndstoneBricksBlock.class,
        EnderGlobeSpawnBlock.class,
        GiddyStatueBlock.class,
        IgniterBlock.class,
        KeepingCastleSpawnBlock.class,
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
        SporeVaporizerBlock.class,
        TeleporterBlockBlock.class,
        TransientBlockCloseBlock.class,
        TransientBlockOpenBlock.class
})
public abstract class BlockLootDataDrivenMixin extends BlockBehaviour {
    protected BlockLootDataDrivenMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "getDrops", at = @At("HEAD"), cancellable = true)
    private void sd$useDataDrivenLootTable(
            BlockState state,
            LootParams.Builder builder,
            CallbackInfoReturnable<List<ItemStack>> cir
    ) {
        cir.setReturnValue(super.getDrops(state, builder));
    }
}
