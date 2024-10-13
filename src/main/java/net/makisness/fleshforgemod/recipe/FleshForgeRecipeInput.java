package net.makisness.fleshforgemod.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record FleshForgeRecipeInput (ItemStack input) implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        return input;
    }

    @Override
    public int size() {
        return 1;
    }

}
