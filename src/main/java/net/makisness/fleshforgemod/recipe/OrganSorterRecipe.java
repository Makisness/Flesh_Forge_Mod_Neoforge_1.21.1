package net.makisness.fleshforgemod.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.*;

public record OrganSorterRecipe( ItemStack inputItem, Map<ResourceLocation, Integer> outputs, int rightClickCount) implements Recipe<RecipeInput> {
    static ResourceLocation resultingItem = ResourceLocation.withDefaultNamespace("rotten_flesh");

    public static ResourceLocation getRandomItem() {
        return resultingItem;
    }

    // An in-code representation of our recipe data. This can be basically anything you want.
    // Common things to have here is a processing time integer of some kind, or an experience reward.
    // Note that we now use an ingredient instead of an item stack for the input.

    // Add a constructor that sets all properties.


    // A list of our ingredients. Does not need to be overridden if you have no ingredients
    // (the default implementation returns an empty list here). It makes sense to cache larger lists in a field.
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(Ingredient.of(this.inputItem));
        return list;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ORGAN_SORTER_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.ORGAN_SORTER_TYPE.get();
    }


    // Grid-based recipes should return whether their recipe can fit in the given dimensions.
    // We don't have a grid, so we just return if any item can be placed in there.
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    // Check whether the given input matches this recipe. The first parameter matches the generic.
    // We check our blockstate and our item stack, and only return true if both match.
    @Override
    public boolean matches(RecipeInput input, Level level) {
        return Ingredient.of(this.inputItem).test(inputItem);
    }

    // Return an UNMODIFIABLE version of your result here. The result of this method is mainly intended
    // for the recipe book, and commonly used by JEI and other recipe viewers as well.
    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return BuiltInRegistries.ITEM.get(getWeightedRandomOutput()).getDefaultInstance();
    }

    // Return the result of the recipe here, based on the given input. The first parameter matches the generic.
    // IMPORTANT: Always call .copy() if you use an existing result! If you don't, things can and will break,
    // as the result exists once per recipe, but the assembled stack is created each time the recipe is crafted.
    @Override
    public ItemStack assemble(RecipeInput input, HolderLookup.Provider registries) {
        ItemStack resulting = BuiltInRegistries.ITEM.get(getWeightedRandomOutput()).getDefaultInstance();
        return resulting.copy();
    }

    public Ingredient getInputItem() {
        return Ingredient.of(inputItem);
    }

    public Map<ResourceLocation, Integer> getOutputWithWeights() {
        return outputs;
    }

    public int getRightClickCount() {
        return rightClickCount;
    }

    // Method to get a weighted random output
    public ResourceLocation getWeightedRandomOutput() {
        int totalWeight = 0;
        for (Integer weight : outputs.values()) {
            totalWeight += weight;
        }

        // Generate a random number between 0 and totalWeight - 1
        Random random = new Random();
        int rand = random.nextInt(totalWeight);

        // Iterate over the entries in the map, reducing rand until it becomes negative
        for (Map.Entry<ResourceLocation, Integer> entry : outputs.entrySet()) {
            rand -= entry.getValue();
            if (rand < 0) {
                resultingItem = entry.getKey();
                return entry.getKey();  // Return the ItemStack that matches the range
            }
        }

        return ResourceLocation.withDefaultNamespace("air");  // Fallback (though rand should always end up in a valid range)
    }
}
