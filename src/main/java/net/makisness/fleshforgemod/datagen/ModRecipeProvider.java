package net.makisness.fleshforgemod.datagen;

import net.makisness.fleshforgemod.fleshforgemod;
import net.makisness.fleshforgemod.item.ModItems;
import net.makisness.fleshforgemod.recipe.FleshForgeRecipeBuilder;
import net.makisness.fleshforgemod.recipe.OrganSorterRecipeBuilder;
import net.makisness.fleshforgemod.recipe.ShapelessBucketBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  ModItems.ROUND_KNIFE.get())
                .pattern("I ")
                .pattern("SI")
                .define('I',Items.IRON_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .unlockedBy("has_stick", has(Items.STICK))
                .save(recipeOutput, "roundknife_from_shaped");

        ShapelessBucketBuilder.shapelessBucket(RecipeCategory.MISC,ModItems.BUCKET_FLESH.get(),1)
                .requires(Items.WATER_BUCKET)
                .requires(ModItems.FLESH_MASS)
                .requires(Items.BONE)
                .unlockedBy("has_flesh_mass", has(ModItems.FLESH_MASS))
                .unlockedBy("has_water_bucket", has(Items.WATER_BUCKET))
                .unlockedBy("has_bone", has(Items.BONE))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.MUSCLE_FIBERS.get(),1)
                .requires(ModItems.ROUND_KNIFE)
                .requires(Items.ROTTEN_FLESH)
                .unlockedBy("has_round_knife", has(ModItems.ROUND_KNIFE))
                .unlockedBy("has_rotten_flesh", has(Items.ROTTEN_FLESH))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.MUSCLE_FIBERS.get(),3)
                .requires(ModItems.ROUND_KNIFE)
                .requires(ModItems.FRESH_FLESH)
                .unlockedBy("has_round_knife", has(ModItems.ROUND_KNIFE))
                .unlockedBy("has_fresh_flesh", has(ModItems.FRESH_FLESH))
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

        new FleshForgeRecipeBuilder(List.of(Ingredient.of(ModItems.FLESH_MASS)),
                new ItemStack(ModItems.BIO_CPU.get()),200)
                .unlockedBy("has_flesh_mass", has(ModItems.FLESH_MASS))
                .save(recipeOutput);



        //ORGAN SORTER RECIPE FOR FLESH MASS
        // Define the outputs with weights
        Map<ResourceLocation, Integer> outputItems = new HashMap<>();
        outputItems.put(ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID,"muscle_fibers"), 4);
        outputItems.put(ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID,"flesh"), 1);
        outputItems.put(ResourceLocation.withDefaultNamespace("bone"), 3);
        outputItems.put(ResourceLocation.withDefaultNamespace("bone"), 3);

        new OrganSorterRecipeBuilder(ModItems.FLESH_MASS.get().getDefaultInstance(),outputItems,5)
                .unlockedBy("has_flesh_mass", has(ModItems.FLESH_MASS))
                .save(recipeOutput);
    }
}
