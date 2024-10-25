package net.makisness.fleshforgemod.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class SimpleRecipeBuilderMultiOut implements RecipeBuilder {
    protected final Map<ResourceLocation, Integer> result;
    protected final Map<String, Criterion<?>> critera = new LinkedHashMap<>();
    @Nullable

    public SimpleRecipeBuilderMultiOut(Map<ResourceLocation, Integer> result){
        this.result = result;
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.critera.put(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        return this;
    }

    @Override
    public Item getResult() {
        // Calculate the weighted random output
        ItemStack selectedStack = BuiltInRegistries.ITEM.get(OrganSorterRecipe.getRandomItem()).getDefaultInstance();
        return selectedStack.getItem();

    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
    }
}
