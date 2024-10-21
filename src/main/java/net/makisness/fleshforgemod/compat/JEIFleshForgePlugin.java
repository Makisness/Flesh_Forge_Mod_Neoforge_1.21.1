package net.makisness.fleshforgemod.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.makisness.fleshforgemod.fleshforgemod;
import net.makisness.fleshforgemod.recipe.FleshForgeRecipe;
import net.makisness.fleshforgemod.recipe.ModRecipes;
import net.makisness.fleshforgemod.screen.custom.FleshForgeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIFleshForgePlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FleshForgeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        List<FleshForgeRecipe>  fleshForgeRecipes = recipeManager
                .getAllRecipesFor(ModRecipes.FLESHFORGE_TYPE.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(FleshForgeRecipeCategory.FLESH_FORGE_RECIPE_RECIPE_TYPE, fleshForgeRecipes);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(FleshForgeScreen.class, 80,30,25,20,
                FleshForgeRecipeCategory.FLESH_FORGE_RECIPE_RECIPE_TYPE);
    }
}
