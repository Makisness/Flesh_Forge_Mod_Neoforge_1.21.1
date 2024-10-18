package net.makisness.fleshforgemod.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.*;

public class ShapelessBucketBuilder extends ShapelessRecipeBuilder {
    private final RecipeCategory category;
    private final Item result;
    private final int count;
    private final ItemStack resultStack;
    private final NonNullList<Ingredient> ingredients;
    private final Map<String, Criterion<?>> criteria;
    @Nullable
    private String group;

    public ShapelessBucketBuilder(RecipeCategory category, int count, ItemStack resultStack, NonNullList<Ingredient> ingredients,Map<String, Criterion<?>> criteria) {
        super(category, resultStack.getItem(), count);
        this.category = category;
        this.result = resultStack.getItem();
        this.count = count;
        this.resultStack = resultStack;
        this.ingredients = NonNullList.create();
        this.criteria = new LinkedHashMap();

    }



    @Override
    public void save(RecipeOutput output, ResourceLocation id) {
        // Build the advancement.
        Advancement.Builder advancement = output.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);
        // Our factory parameters are the result, the block state, and the ingredient.
        ShapelessBucketRecipe shapelessrecipe = new ShapelessBucketRecipe((String) Objects.requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category), this.resultStack.getItem(), this.ingredients);
        // Pass the id, the recipe, and the recipe advancement into the RecipeOutput.
        output.accept(id, shapelessrecipe, advancement.build(id.withPrefix("recipes/")));
    }
    public static List<ItemStack> convertToItemStackList(NonNullList<Ingredient> ingredients) {
        List<ItemStack> itemStacks = new ArrayList<>();

        for (Ingredient ingredient : ingredients) {
            // Get the matching ItemStacks for each Ingredient (in this case, just the first match)
            ItemStack[] matchingStacks = ingredient.getItems();
            if (matchingStacks.length > 0) {
                // Add the first matching ItemStack to the list
                itemStacks.add(matchingStacks[0]);
            }
        }

        return itemStacks;
    }
}

