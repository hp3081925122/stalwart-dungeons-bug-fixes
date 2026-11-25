package com.hp.stalwartdungeonsbugfix.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import javax.annotation.Nullable;

/**
 * 运行时基岩保护结构处理器：放置地牢模板时跳过目标位置已是基岩的方块，防止击穿下界基岩顶与基岩底。
 */
public final class SdBedrockProtectProcessor extends StructureProcessor {
    public static final SdBedrockProtectProcessor INSTANCE = new SdBedrockProtectProcessor();

    private SdBedrockProtectProcessor() {
    }

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(
            LevelReader level,
            BlockPos offset,
            BlockPos pos,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructureTemplate.StructureBlockInfo relativeBlockInfo,
            StructurePlaceSettings settings
    ) {
        if (level.getBlockState(relativeBlockInfo.pos()).is(Blocks.BEDROCK)) {
            return null;
        }
        return relativeBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return StructureProcessorType.BLOCK_IGNORE;
    }
}
