package net.makisness.fleshforgemod.block.entity;

import net.makisness.fleshforgemod.block.ModBlocks;
import net.makisness.fleshforgemod.block.entity.custom.FleshForgeBlockEntity;
import net.makisness.fleshforgemod.block.entity.custom.FleshGeneratorBlockEntity;
import net.makisness.fleshforgemod.block.entity.custom.TestBlockEntity;
import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, fleshforgemod.MODID);

    public static final Supplier<BlockEntityType<FleshForgeBlockEntity>> FLESH_FORGE_BE =
            BLOCK_ENTITIES.register("flesh_forge_be", () -> BlockEntityType.Builder.of(
                    FleshForgeBlockEntity::new, ModBlocks.FLESH_FORGE.get()).build(null));

    public static final Supplier<BlockEntityType<FleshGeneratorBlockEntity>> FLESH_GENERATOR_BE =
            BLOCK_ENTITIES.register("flesh_generator_be", () -> BlockEntityType.Builder.of(
                    FleshGeneratorBlockEntity::new, ModBlocks.FLESH_GENERATOR.get()).build(null));

    public static final Supplier<BlockEntityType<TestBlockEntity>> TEST_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("test_block", () -> BlockEntityType.Builder.of(
                    TestBlockEntity::new, ModBlocks.TEST_BLOCK.get()).build(null));


    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
