package com.hp.stalwartdungeonsbugfix.block;

import net.minecraft.world.level.material.MapColor;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * 坚固地牢全部 39 个方块的原版 MapColor 静态映射表（依据各材质主色调与对应原版方块语义匹配）。
 */
public final class SdBlockMapColorHelper {
    private static final Map<String, MapColor> BLOCK_MAP_COLORS = Map.ofEntries(
            // 灵魂砖与灵魂瓦系（对应原版灵魂沙/灵魂土 COLOR_BROWN）
            Map.entry("SoulBricksBlock", MapColor.COLOR_BROWN),
            Map.entry("SoulBricksStairsBlock", MapColor.COLOR_BROWN),
            Map.entry("SoulBricksSlabBlock", MapColor.COLOR_BROWN),
            Map.entry("SoulBricksWallBlock", MapColor.COLOR_BROWN),
            Map.entry("SoulTilesBlock", MapColor.COLOR_BROWN),
            Map.entry("SoulTilesSlabBlock", MapColor.COLOR_BROWN),
            Map.entry("IgniterBlock", MapColor.COLOR_BROWN),
            Map.entry("PusherBlock", MapColor.COLOR_BROWN),
            Map.entry("AwfulGhastAltarBlock", MapColor.COLOR_BROWN),
            Map.entry("SoulMagmaBricksBlock", MapColor.TERRACOTTA_CYAN),
            Map.entry("TungstenOreBlock", MapColor.DEEPSLATE),

            // 下界砖与守望城堡系（对应原版下界砖/下界岩 NETHER）
            Map.entry("NetherBricksAndNetherrackBlock", MapColor.NETHER),
            Map.entry("NetherBricksAndNetherrackStairsBlock", MapColor.NETHER),
            Map.entry("NetherBricksAndNetherrackSlabBlock", MapColor.NETHER),
            Map.entry("NetherBricksAndSoulSandBlock", MapColor.NETHER),
            Map.entry("NetherBricksAndSoulSandStairsBlock", MapColor.NETHER),
            Map.entry("NetherBricksAndSoulSandSlabBlock", MapColor.NETHER),
            Map.entry("NetherKeeperAltarBlock", MapColor.NETHER),
            Map.entry("GiddyStatueBlock", MapColor.NETHER),
            Map.entry("SporeVaporizerBlock", MapColor.NETHER),
            Map.entry("TransientBlockCloseBlock", MapColor.NETHER),
            Map.entry("TransientBlockOpenBlock", MapColor.COLOR_ORANGE),

            // 末地石与紫珀砖系（对应原版末地石 SAND / 紫珀 COLOR_MAGENTA / 黑曜石 COLOR_BLACK）
            Map.entry("ChiseledEndStoneBricksBlock", MapColor.SAND),
            Map.entry("CrackedEndstoneBricksBlock", MapColor.SAND),
            Map.entry("SheltererAltarBlock", MapColor.SAND),
            Map.entry("PurpurBricksBlock", MapColor.COLOR_MAGENTA),
            Map.entry("PurpurBricksStairsBlock", MapColor.COLOR_MAGENTA),
            Map.entry("PurpurBricksSlabBlock", MapColor.COLOR_MAGENTA),
            Map.entry("PurpurBricksWallBlock", MapColor.COLOR_MAGENTA),
            Map.entry("PropulserBlockBlock", MapColor.COLOR_MAGENTA),
            Map.entry("ChorundumOreBlock", MapColor.COLOR_MAGENTA),
            Map.entry("TeleporterBlockBlock", MapColor.COLOR_MAGENTA),
            Map.entry("PurpurBricksAndEndstoneBlock", MapColor.TERRACOTTA_WHITE),
            Map.entry("PurpurBricksAndEndstoneStairsBlock", MapColor.TERRACOTTA_WHITE),
            Map.entry("PurpurBricksAndEndstoneSlabBlock", MapColor.TERRACOTTA_WHITE),
            Map.entry("ObsidianStrikerBlock", MapColor.COLOR_BLACK),

            // 内部地牢触发方块（创造放置时也可正常在地图显示）
            Map.entry("AwfulDungeonsSpawnBlock", MapColor.STONE),
            Map.entry("KeepingCastleSpawnBlock", MapColor.STONE),
            Map.entry("EnderGlobeSpawnBlock", MapColor.STONE)
    );

    private SdBlockMapColorHelper() {
    }

    @Nullable
    public static MapColor resolveMapColor(String simpleClassName) {
        return BLOCK_MAP_COLORS.get(simpleClassName);
    }
}
