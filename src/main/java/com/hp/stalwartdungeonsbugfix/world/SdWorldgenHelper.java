package com.hp.stalwartdungeonsbugfix.world;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/**
 * 坚固地牢世界生成与触发方块结构放置修复工具：
 * 1. 使用基于世界种子与区块网格的确定性间距判定，彻底防止同类地牢相邻重叠互切；
 * 2. 下界地牢仅在下界生成并搜索安全高度与实体支撑地面，避免击穿 Y=127 基岩顶、Y=0..4 基岩底、高空悬空或沉没岩浆海；
 * 3. 末地地牢主动跳过 the_void 群系与末地虚空区域，要求真实末地岛表面并修复四方向随机旋转；
 * 4. 触发方块放置 2~4 万方块大型地牢时仅在服务端运行、先移除触发块再放置、忽略结构方块、保护基岩并使用 flag=2 降低级联更新卡顿。
 */
public final class SdWorldgenHelper {
    private static final ResourceLocation AWFUL_SPAWN = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "awful_dungeon_spawn");
    private static final ResourceLocation KEEPING_SPAWN = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "keeping_castle_spawn");
    private static final ResourceLocation END_SPAWN = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "end_dungeon_spawn");

    private static final ResourceLocation AWFUL_DUNGEON = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "awful_dungeon");
    private static final ResourceLocation KEEPING_CASTLE = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "keeping_castle");
    private static final ResourceLocation SHELL_GLOBE = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "shell_globe");

    private static final long SALT_AWFUL = 0x51A7B3C9L;
    private static final long SALT_KEEPING = 0x2F84D6E1L;
    private static final long SALT_END = 0x7C9E14A5L;

    private SdWorldgenHelper() {
    }

    /**
     * 判断当前区块是否为指定间距网格中选中的候选生成区块（带边沿隔离，保证相邻两格地牢不会互切）。
     */
    public static boolean isGridAnchorChunk(long seed, int chunkX, int chunkZ, int spacingChunks, long salt) {
        int spacing = Math.max(6, spacingChunks);
        int cellX = Math.floorDiv(chunkX, spacing);
        int cellZ = Math.floorDiv(chunkZ, spacing);
        int margin = Math.min(2, (spacing - 2) / 2);
        int usableSpan = Math.max(1, spacing - margin * 2);

        long hash = seed ^ (cellX * 341873128712L) ^ (cellZ * 132897987541L) ^ salt;
        RandomSource cellRandom = RandomSource.create(hash);
        int targetOffsetX = margin + cellRandom.nextInt(usableSpan);
        int targetOffsetZ = margin + cellRandom.nextInt(usableSpan);

        int localX = Math.floorMod(chunkX, spacing);
        int localZ = Math.floorMod(chunkZ, spacing);
        return localX == targetOffsetX && localZ == targetOffsetZ;
    }

    public static boolean placeNetherDungeonAnchor(
            FeaturePlaceContext<NoneFeatureConfiguration> context,
            boolean isAwfulDungeon
    ) {
        WorldGenLevel level = context.level();
        ServerLevel serverLevel = level.getLevel();
        ResourceKey<Level> dimension = serverLevel.dimension();
        if (!Level.NETHER.equals(dimension)) {
            return false;
        }

        ChunkPos chunkPos = new ChunkPos(context.origin());
        int spacing = isAwfulDungeon ? SdBugfixConfig.getAwfulDungeonSpacingChunks() : SdBugfixConfig.getKeepingCastleSpacingChunks();
        long salt = isAwfulDungeon ? SALT_AWFUL : SALT_KEEPING;
        if (!isGridAnchorChunk(serverLevel.getSeed(), chunkPos.x, chunkPos.z, spacing, salt)) {
            return false;
        }

        StructureTemplate template = serverLevel.getStructureManager().getOrCreate(isAwfulDungeon ? AWFUL_SPAWN : KEEPING_SPAWN);
        if (template == null) {
            return false;
        }

        int x = chunkPos.getMinBlockX() + 5 + context.random().nextInt(6);
        int z = chunkPos.getMinBlockZ() + 5 + context.random().nextInt(6);

        int minY = Math.max(level.getMinBuildHeight() + 12, SdBugfixConfig.getNetherDungeonMinY());
        int maxY = Math.min(100, SdBugfixConfig.getNetherDungeonMaxY());
        if (maxY < minY) {
            maxY = minY;
        }

        int chosenY = findSafeNetherSurfaceY(level, x, z, minY, maxY, context.random());
        if (chosenY < minY) {
            return false;
        }

        BlockPos spawnTo = new BlockPos(x, chosenY, z);
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setMirror(Mirror.NONE)
                .setRotation(Rotation.NONE)
                .setRandom(context.random())
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                .addProcessor(SdBedrockProtectProcessor.INSTANCE)
                .setIgnoreEntities(false);

        return template.placeInWorld(level, spawnTo, spawnTo, settings, context.random(), Block.UPDATE_CLIENTS);
    }

    /**
     * 在下界合法 Y 范围内寻找带有实体地面支撑且上方有空间的落脚点，尽量避免悬空与岩浆淹没。
     */
    private static int findSafeNetherSurfaceY(WorldGenLevel level, int x, int z, int minY, int maxY, RandomSource random) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int fallbackY = -1;

        for (int y = minY; y <= maxY; y++) {
            cursor.set(x, y - 1, z);
            BlockState below = level.getBlockState(cursor);
            if (!below.isSolidRender(level, cursor) || below.is(Blocks.BEDROCK) || !below.getFluidState().isEmpty()) {
                continue;
            }
            cursor.set(x, y, z);
            BlockState at = level.getBlockState(cursor);
            if (!at.getFluidState().isEmpty()) {
                continue;
            }
            cursor.set(x, y + 1, z);
            BlockState above = level.getBlockState(cursor);
            if (at.isAir() && above.isAir()) {
                // 优先选择地表空腔处的实体平台
                if (fallbackY < 0 || random.nextInt(3) == 0) {
                    fallbackY = y;
                }
            } else if (fallbackY < 0 && !above.is(Blocks.BEDROCK) && above.getFluidState().isEmpty()) {
                // 其次接受非岩浆实心地层内部，避免完全错过生成
                fallbackY = y;
            }
        }
        return fallbackY;
    }

    public static boolean placeEndDungeonAnchor(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        ServerLevel serverLevel = level.getLevel();
        if (!Level.END.equals(serverLevel.dimension())) {
            return false;
        }

        Holder<Biome> biome = level.getBiome(context.origin());
        if (biome.is(Biomes.THE_VOID)) {
            return false;
        }

        ChunkPos chunkPos = new ChunkPos(context.origin());
        // 避开末地主岛核心区域，防止破坏末影龙战场地
        if ((long) chunkPos.x * chunkPos.x + (long) chunkPos.z * chunkPos.z < 400L) {
            return false;
        }

        int spacing = SdBugfixConfig.getEndDungeonSpacingChunks();
        if (!isGridAnchorChunk(serverLevel.getSeed(), chunkPos.x, chunkPos.z, spacing, SALT_END)) {
            return false;
        }

        int x = chunkPos.getMinBlockX() + 6 + context.random().nextInt(4);
        int z = chunkPos.getMinBlockZ() + 6 + context.random().nextInt(4);
        int surfaceY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
        if (surfaceY < SdBugfixConfig.getEndDungeonMinSurfaceY()) {
            return false;
        }

        BlockPos groundPos = new BlockPos(x, surfaceY - 1, z);
        BlockState groundState = level.getBlockState(groundPos);
        if (!groundState.isSolidRender(level, groundPos) || groundState.is(Blocks.BEDROCK)) {
            return false;
        }

        StructureTemplate template = serverLevel.getStructureManager().getOrCreate(END_SPAWN);
        if (template == null) {
            return false;
        }

        BlockPos spawnTo = new BlockPos(x, surfaceY - 1, z);
        Rotation rotation = Rotation.values()[context.random().nextInt(Rotation.values().length)];
        Mirror mirror = Mirror.values()[context.random().nextInt(2)];
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setMirror(mirror)
                .setRotation(rotation)
                .setRandom(context.random())
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                .addProcessor(SdBedrockProtectProcessor.INSTANCE)
                .setIgnoreEntities(false);

        return template.placeInWorld(level, spawnTo, spawnTo, settings, context.random(), Block.UPDATE_CLIENTS);
    }

    public static void placeAwfulDungeonFromTrigger(LevelAccessor world, double x, double y, double z) {
        placeDungeonSafely(world, x, y, z, AWFUL_DUNGEON, -30, -7, -30);
    }

    public static void placeKeepingCastleFromTrigger(LevelAccessor world, double x, double y, double z) {
        placeDungeonSafely(world, x, y, z, KEEPING_CASTLE, -17, -6, -32);
    }

    public static void placeEndDungeonFromTrigger(LevelAccessor world, double x, double y, double z) {
        placeDungeonSafely(world, x, y, z, SHELL_GLOBE, -26, -18, -23);
    }

    private static void placeDungeonSafely(
            LevelAccessor world,
            double x,
            double y,
            double z,
            ResourceLocation templateId,
            int offsetX,
            int offsetY,
            int offsetZ
    ) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos triggerPos = BlockPos.containing(x, y, z);
        // 先移除触发方块再放置地牢模板，避免在可怕地牢放置完成后再挖洞
        serverLevel.setBlock(triggerPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);

        StructureTemplate template = serverLevel.getStructureManager().getOrCreate(templateId);
        if (template == null) {
            return;
        }

        BlockPos origin = BlockPos.containing(x + offsetX, y + offsetY, z + offsetZ);
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(Rotation.NONE)
                .setMirror(Mirror.NONE)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                .addProcessor(SdBedrockProtectProcessor.INSTANCE)
                .setIgnoreEntities(false);

        // 使用 UPDATE_CLIENTS (2) 而非 UPDATE_ALL (3)，避免数万方块同步触发邻接更新造成服务器冻结
        template.placeInWorld(serverLevel, origin, origin, settings, serverLevel.random, Block.UPDATE_CLIENTS);
    }
}
