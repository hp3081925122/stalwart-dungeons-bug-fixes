package com.hp.stalwartdungeonsbugfix.world;

import com.hp.stalwartdungeonsbugfix.Stalwartdungeonsbugfix;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 原生 Minecraft StructureType 与 StructurePieceType 注册表：
 * 让坚固地牢的三座大型地牢正式接入原版结构系统（支持 /locate、探险家指南针、FTB Quests 与分区块渐进生成）。
 */
public final class SdWorldgenRegistry {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, Stalwartdungeonsbugfix.MOD_ID);
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, Stalwartdungeonsbugfix.MOD_ID);

    public static final RegistryObject<StructureType<SdDungeonStructure>> DUNGEON_STRUCTURE =
            STRUCTURE_TYPES.register("dungeon", () -> () -> SdDungeonStructure.CODEC);

    public static final RegistryObject<StructurePieceType> DUNGEON_PIECE =
            STRUCTURE_PIECES.register("dungeon_piece", () -> (StructurePieceType.StructureTemplateType) SdDungeonPiece::new);

    private SdWorldgenRegistry() {
    }

    public static void register(IEventBus modBus) {
        STRUCTURE_TYPES.register(modBus);
        STRUCTURE_PIECES.register(modBus);
    }
}
