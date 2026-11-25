package com.hp.stalwartdungeonsbugfix.world;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

/**
 * 坚固地牢原生 Structure 实现：
 * 绕过 MCreator 32x32 限制与延迟触发方块卡顿，直接在世界生成阶段通过噪声列与高度图定位安全落脚点，
 * 完整支持 /locate structure、探险家指南针、FTB Quests、结构集间距防重叠以及全部四方向旋转。
 */
public final class SdDungeonStructure extends Structure {
    public static final String MODE_NETHER_CAVERN = "nether_cavern";
    public static final String MODE_END_ISLAND = "end_island";

    public static final Codec<SdDungeonStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            settingsCodec(instance),
            ResourceLocation.CODEC.fieldOf("template").forGetter(s -> s.template),
            Codec.STRING.fieldOf("placement_mode").forGetter(s -> s.placementMode),
            Codec.INT.optionalFieldOf("y_offset", 0).forGetter(s -> s.yOffset),
            Codec.INT.optionalFieldOf("min_distance_from_origin", 0).forGetter(s -> s.minDistanceFromOrigin),
            Codec.BOOL.optionalFieldOf("allow_rotation", true).forGetter(s -> s.allowRotation)
    ).apply(instance, SdDungeonStructure::new));

    private final ResourceLocation template;
    private final String placementMode;
    private final int yOffset;
    private final int minDistanceFromOrigin;
    private final boolean allowRotation;

    public SdDungeonStructure(
            StructureSettings settings,
            ResourceLocation template,
            String placementMode,
            int yOffset,
            int minDistanceFromOrigin,
            boolean allowRotation
    ) {
        super(settings);
        this.template = template;
        this.placementMode = placementMode;
        this.yOffset = yOffset;
        this.minDistanceFromOrigin = minDistanceFromOrigin;
        this.allowRotation = allowRotation;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (!SdBugfixConfig.isUseNativeStructuresEnabled()) {
            return Optional.empty();
        }

        StructureTemplate structureTemplate = context.structureTemplateManager().getOrCreate(this.template);
        if (structureTemplate == null || structureTemplate.getSize().getY() <= 0) {
            return Optional.empty();
        }

        int centerX = context.chunkPos().getMiddleBlockX();
        int centerZ = context.chunkPos().getMiddleBlockZ();
        if (this.minDistanceFromOrigin > 0) {
            long distSq = (long) centerX * centerX + (long) centerZ * centerZ;
            long minDistSq = (long) this.minDistanceFromOrigin * this.minDistanceFromOrigin;
            if (distSq < minDistSq) {
                return Optional.empty();
            }
        }

        Rotation rotation = this.allowRotation ? Rotation.getRandom(context.random()) : Rotation.NONE;
        BoundingBox zeroBox = structureTemplate.getBoundingBox(SdDungeonPiece.makeSettings(rotation), BlockPos.ZERO);
        int sizeY = Math.max(1, zeroBox.getYSpan());

        int floorY;
        int baseY;
        if (MODE_END_ISLAND.equals(this.placementMode)) {
            int surfaceY = findSafeEndIslandSurfaceY(context, centerX, centerZ);
            if (surfaceY < 0) {
                return Optional.empty();
            }
            floorY = surfaceY;
            int minAllowedY = Math.max(context.heightAccessor().getMinBuildHeight() + 8, 20);
            int maxAllowedY = Math.max(minAllowedY, context.heightAccessor().getMaxBuildHeight() - sizeY - 4);
            baseY = Mth.clamp(floorY + this.yOffset, minAllowedY, maxAllowedY);
        } else {
            floorY = findSafeNetherFloorY(context, centerX, centerZ, sizeY);
            if (floorY < 0) {
                return Optional.empty();
            }
            int minAllowedY = Math.max(context.heightAccessor().getMinBuildHeight() + 10, 10);
            int maxAllowedY = Math.max(minAllowedY, 120 - sizeY);
            baseY = Mth.clamp(floorY + this.yOffset, minAllowedY, maxAllowedY);
        }

        // 按旋转后的真实包围盒精确对齐到区块中心与目标底面高度，避免四方向旋转发生象限偏移
        int shiftX = centerX - (zeroBox.minX() + zeroBox.maxX()) / 2;
        int shiftZ = centerZ - (zeroBox.minZ() + zeroBox.maxZ()) / 2;
        int shiftY = baseY - zeroBox.minY();
        BlockPos templatePosition = new BlockPos(shiftX, shiftY, shiftZ);
        BlockPos locatorPos = new BlockPos(centerX, floorY, centerZ);

        return Optional.of(new GenerationStub(locatorPos, builder ->
                builder.addPiece(new SdDungeonPiece(context.structureTemplateManager(), this.template, templatePosition, rotation))
        ));
    }

    /**
     * 在下界噪声列中寻找安全实体地面（优先洞穴开放地面，其次非岩浆实心地层），
     * 并确保整个地牢高度完全处于基岩底（Y=0..4）与基岩顶（Y=123..127）之间。
     */
    private int findSafeNetherFloorY(GenerationContext context, int x, int z, int sizeY) {
        LevelHeightAccessor heightAccessor = context.heightAccessor();
        int minBaseY = Math.max(heightAccessor.getMinBuildHeight() + 10, 12);
        int maxBaseY = Math.max(minBaseY, 120 - sizeY);

        int minFloorY = Math.max(SdBugfixConfig.getNetherDungeonMinY(), minBaseY - this.yOffset);
        int maxFloorY = Math.min(SdBugfixConfig.getNetherDungeonMaxY(), maxBaseY - this.yOffset);
        if (maxFloorY < minFloorY) {
            minFloorY = Math.max(32, minBaseY - this.yOffset);
            maxFloorY = Math.max(minFloorY, maxBaseY - this.yOffset);
        }

        NoiseColumn column = context.chunkGenerator().getBaseColumn(x, z, heightAccessor, context.randomState());
        int preferredY = -1;
        int solidFallbackY = -1;
        int openFloorCount = 0;

        for (int y = minFloorY; y <= maxFloorY; y++) {
            BlockState below = column.getBlock(y - 1);
            if (below.isAir() || below.is(Blocks.BEDROCK) || !below.getFluidState().isEmpty()) {
                continue;
            }
            BlockState at = column.getBlock(y);
            BlockState above = column.getBlock(y + 1);
            if (!at.getFluidState().isEmpty() || !above.getFluidState().isEmpty()) {
                continue;
            }
            if (at.isAir() && above.isAir()) {
                openFloorCount++;
                // 蓄水池抽样，保证下界多层洞穴高度均匀分布
                if (context.random().nextInt(openFloorCount) == 0) {
                    preferredY = y;
                }
            } else if (solidFallbackY < 0 && !at.is(Blocks.BEDROCK) && !above.is(Blocks.BEDROCK)) {
                solidFallbackY = y;
            }
        }

        return preferredY >= 0 ? preferredY : solidFallbackY;
    }

    /**
     * 在末地采样中心点及四周 12 格邻点高度，确保结构落在真实末地岛屿而非虚空或极窄浮石边缘。
     */
    private int findSafeEndIslandSurfaceY(GenerationContext context, int x, int z) {
        ChunkGenerator generator = context.chunkGenerator();
        LevelHeightAccessor heightAccessor = context.heightAccessor();
        RandomState randomState = context.randomState();
        int minSurfaceY = SdBugfixConfig.getEndDungeonMinSurfaceY();

        int centerY = generator.getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState);
        if (centerY < minSurfaceY) {
            return -1;
        }

        int validNeighbors = 0;
        int[][] offsets = {{12, 0}, {-12, 0}, {0, 12}, {0, -12}};
        for (int[] offset : offsets) {
            int ny = generator.getFirstOccupiedHeight(
                    x + offset[0],
                    z + offset[1],
                    Heightmap.Types.WORLD_SURFACE_WG,
                    heightAccessor,
                    randomState
            );
            if (ny >= minSurfaceY) {
                validNeighbors++;
            }
        }
        return validNeighbors >= 2 ? centerY : -1;
    }

    @Override
    public StructureType<?> type() {
        return SdWorldgenRegistry.DUNGEON_STRUCTURE.get();
    }
}
