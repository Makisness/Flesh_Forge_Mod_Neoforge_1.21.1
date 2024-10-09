package net.makisness.fleshforgemod.block.entity;

import net.makisness.fleshforgemod.block.ModBlocks;
import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, fleshforgemod.MODID);

    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FleshForgeBlockEntity>> FLESH_FORGE_BE =
            BLOCK_ENTITIES.register("flesh_forge_be",
                    () -> BlockEntityType.Builder.of(FleshForgeBlockEntity::new, ModBlocks.FLESH_FORGE.get()).build(null));


    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
