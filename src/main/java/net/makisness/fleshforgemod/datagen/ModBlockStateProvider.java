package net.makisness.fleshforgemod.datagen;

import net.makisness.fleshforgemod.block.ModBlocks;
import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
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

        simpleBlock(ModBlocks.GEM_POLISHING_STATION.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/gem_polishing_station"))); {
                }
        simpleBlock(ModBlocks.FLESH_FORGE.get(),
                new ModelFile.UncheckedModelFile(modLoc("block/flesh_forge"))); {
        }
    }


    private void blockWithItem(DeferredBlock<?> deferredBlock){
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
