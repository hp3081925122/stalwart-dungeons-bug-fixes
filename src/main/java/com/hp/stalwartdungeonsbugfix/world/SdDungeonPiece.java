package com.hp.stalwartdungeonsbugfix.world;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

/**
 * 坚固地牢原生结构片段：
 * 直接零拷贝加载原模组的完整 NBT 模板（60x31x60 / 35x29x65 / 55x47x46），
 * 随区块生成按 ChunkBox 分块裁剪放置，并挂载基岩保护与忽略结构方块处理器。
 */
public final class SdDungeonPiece extends TemplateStructurePiece {
    private static final String TAG_ROTATION = "Rot";

    public SdDungeonPiece(
            StructureTemplateManager templateManager,
            ResourceLocation templateId,
            BlockPos templatePosition,
            Rotation rotation
    ) {
        super(
                SdWorldgenRegistry.DUNGEON_PIECE.get(),
                0,
                templateManager,
                templateId,
                templateId.toString(),
                makeSettings(rotation),
                templatePosition
        );
    }

    public SdDungeonPiece(StructureTemplateManager templateManager, CompoundTag tag) {
        super(
                SdWorldgenRegistry.DUNGEON_PIECE.get(),
                tag,
                templateManager,
                id -> makeSettings(parseRotation(tag.getString(TAG_ROTATION)))
        );
    }

    public static StructurePlaceSettings makeSettings(Rotation rotation) {
        return new StructurePlaceSettings()
                .setRotation(rotation)
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(false)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK)
                .addProcessor(SdBedrockProtectProcessor.INSTANCE);
    }

    private static Rotation parseRotation(String name) {
        if (name == null || name.isEmpty()) {
            return Rotation.NONE;
        }
        try {
            return Rotation.valueOf(name);
        } catch (IllegalArgumentException ignored) {
            return Rotation.NONE;
        }
    }

    @Override
    protected ResourceLocation makeTemplateLocation() {
        ResourceLocation parsed = ResourceLocation.tryParse(this.templateName);
        return parsed != null ? parsed : ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", this.templateName);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString(TAG_ROTATION, this.placeSettings.getRotation().name());
    }

    @Override
    protected void handleDataMarker(
            String name,
            BlockPos pos,
            ServerLevelAccessor level,
            RandomSource random,
            BoundingBox box
    ) {
        // 原模组三座地牢模板均不含数据标记结构方块，此处保持空实现。
    }
}
