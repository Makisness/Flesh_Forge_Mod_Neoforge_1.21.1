package net.makisness.fleshforgemod.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

public class ShapelessBucketRecipe extends ShapelessRecipe {

    public ShapelessBucketRecipe(String group, CraftingBookCategory category, ItemLike result, NonNullList<Ingredient> ingredients) {
        super(group, category, result.asItem().getDefaultInstance(), ingredients);
    }

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        //iterate through the crafting input
        for (int i = 0; i < nonnulllist.size(); i++) {

            //get itemstack of each index
            ItemStack item = input.getItem(i);

            //check if the itemstack is a water bucket
            if (item.getItem() == Items.WATER_BUCKET) {

                //set the itemstack at that index to empty (for getRemainingItems)
                nonnulllist.set(i, ItemStack.EMPTY);
            }
        }
        return nonnulllist;
    }

    }