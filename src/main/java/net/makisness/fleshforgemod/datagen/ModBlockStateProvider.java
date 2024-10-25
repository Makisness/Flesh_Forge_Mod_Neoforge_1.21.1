package net.makisness.fleshforgemod.datagen;

import net.makisness.fleshforgemod.block.ModBlocks;
import net.makisness.fleshforgemod.block.custom.FleshForgeBlock;
import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, fleshforgemod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        blockWithItem(ModBlocks.BAUXITE_ORE);
        blockWithItem(ModBlocks.DEEPSLATE_BAUXITE_ORE);

        horizontalBlock(ModBlocks.FLESH_GENERATOR.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/flesh_generator")));

        horizontalBlock(ModBlocks.ORGAN_SORTER.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/organ_sorter")));

        // Register the FleshForge block with active and facing states
        registerFleshForgeBlock(ModBlocks.FLESH_FORGE.get());

        // Generate item models for the blocks
        blockItem(ModBlocks.FLESH_FORGE);
        blockItem(ModBlocks.FLESH_GENERATOR);
        blockItem(ModBlocks.ORGAN_SORTER);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("fleshforgemod:block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<Block> deferredBlock, String appendix) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("fleshforgemod:block/" + deferredBlock.getId().getPath() + appendix));
    }

    private void registerFleshForgeBlock(Block block) {
        getVariantBuilder(block)
                .forAllStates(state -> {
                    Direction facing = state.getValue(FleshForgeBlock.FACING);
                    boolean active = state.getValue(FleshForgeBlock.ACTIVE);

                    // Determine which model to use based on the "active" property
                    ModelFile model = active ? new ModelFile.UncheckedModelFile(modLoc("block/flesh_forge_active"))
                            : new ModelFile.UncheckedModelFile(modLoc("block/flesh_forge"));

                    return ConfiguredModel.builder()
                            .modelFile(model)
                            .rotationY((int) facing.getOpposite().toYRot()) // Rotate the model based on the facing property
                            .build();
                });
    }
}