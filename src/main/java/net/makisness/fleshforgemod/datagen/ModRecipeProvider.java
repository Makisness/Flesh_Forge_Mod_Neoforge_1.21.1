package net.makisness.fleshforgemod.datagen;

import net.makisness.fleshforgemod.item.ModItems;
import net.makisness.fleshforgemod.recipe.FleshForgeRecipeBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }


    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BIO_CPU.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B',ModItems.FLESH_MASS.get())
                .unlockedBy("has_flesh_mass", has(ModItems.FLESH_MASS)).save(recipeOutput, "biocpu_from_crafting");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.BUCKET_FLESH.get(),1)
                .requires(Items.WATER_BUCKET)
                .requires(ModItems.FLESH_MASS)
                .requires(Items.BONE)
                .unlockedBy("has_flesh_mass", has(ModItems.FLESH_MASS))
                .unlockedBy("has_water_bucket", has(Items.WATER_BUCKET))
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);

        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(ModItems.BUCKET_FLESH),
                RecipeCategory.MISC, ModItems.BUCKET_GELATIN,
                .1f,
                200)
                .unlockedBy("has_bucket_flesh", has(ModItems.BUCKET_FLESH))
                .save(recipeOutput,"fleshforgemod:gelatin_from_furnace");

        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(ModItems.RAW_BAUXITE),
                RecipeCategory.MISC, ModItems.ALUMINUM_INGOT,
                .1f,
                200)
                .unlockedBy("has_raw_bauxite", has(ModItems.RAW_BAUXITE))
                .save(recipeOutput,"fleshforgemod:aluminum_from_furnace");

        new FleshForgeRecipeBuilder(Ingredient.of(ModItems.FLESH_MASS),
                new ItemStack(ModItems.BIO_CPU.get()),200)
                .unlockedBy("has_flesh_mass", has(ModItems.FLESH_MASS)).save(recipeOutput);

    }
}
