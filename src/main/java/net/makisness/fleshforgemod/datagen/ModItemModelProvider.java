package net.makisness.fleshforgemod.datagen;

import net.makisness.fleshforgemod.fleshforgemod;
import net.makisness.fleshforgemod.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, fleshforgemod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.RAW_BAUXITE.get());
        basicItem(ModItems.BIO_CPU.get());
        basicItem(ModItems.FLESH_MASS.get());
        basicItem(ModItems.BUCKET_FLESH.get());
        basicItem(ModItems.BUCKET_GELATIN.get());
        basicItem(ModItems.ROUND_KNIFE.get());
        basicItem(ModItems.ALUMINUM_INGOT.get());


    }
}
