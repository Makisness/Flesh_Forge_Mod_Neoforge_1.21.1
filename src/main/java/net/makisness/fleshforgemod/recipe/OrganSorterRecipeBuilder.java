package net.makisness.fleshforgemod.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;

public class OrganSorterRecipeBuilder extends SimpleRecipeBuilderMultiOut {
        private final Map<ResourceLocation,Integer> outputItems;
        private final ItemStack inputItem;
        private final int rightClickCount;

        // Since we have exactly one of each input, we pass them to the constructor.
        // Builders for recipe serializers that have ingredient lists of some sort would usually
        // initialize an empty list and have #addIngredient or similar methods instead.
        public OrganSorterRecipeBuilder(ItemStack inputItem, Map<ResourceLocation,Integer> outputItems, Integer rightClickCount) {
            super(outputItems);

            this.outputItems = outputItems;
            this.inputItem = inputItem;
            this.rightClickCount = rightClickCount;
        }

        // Saves a recipe using the given RecipeOutput and id. This method is defined in the RecipeBuilder interface.
        @Override
        public void save(RecipeOutput output, ResourceLocation id) {
            // Build the advancement.
            Advancement.Builder advancement = output.advancement()
                    .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                    .rewards(AdvancementRewards.Builder.recipe(id))
                    .requirements(AdvancementRequirements.Strategy.OR);
            this.critera.forEach(advancement::addCriterion);
            // Our factory parameters are the result, the block state, and the ingredient.
            OrganSorterRecipe recipe = new OrganSorterRecipe(this.inputItem, this.outputItems, this.rightClickCount);
            // Pass the id, the recipe, and the recipe advancement into the RecipeOutput.
            output.accept(id, recipe, advancement.build(id.withPrefix("recipes/")));
        }
    }

