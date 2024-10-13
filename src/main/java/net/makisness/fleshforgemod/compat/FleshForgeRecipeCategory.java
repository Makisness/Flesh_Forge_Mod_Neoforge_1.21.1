package net.makisness.fleshforgemod.compat;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.makisness.fleshforgemod.block.ModBlocks;
import net.makisness.fleshforgemod.fleshforgemod;
import net.makisness.fleshforgemod.recipe.FleshForgeRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class FleshForgeRecipeCategory implements IRecipeCategory<FleshForgeRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID,"biofabricating");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID,
            "textures/gui/flesh_forge/crystallizer_gui.png");

    public static final RecipeType<FleshForgeRecipe> FLESH_FORGE_RECIPE_RECIPE_TYPE =
            new RecipeType<>(UID, FleshForgeRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public FleshForgeRecipeCategory(IGuiHelper helper){
        this.background =  helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.FLESH_FORGE.get()));
    }

    @Override
    public RecipeType<FleshForgeRecipe> getRecipeType() {
        return FLESH_FORGE_RECIPE_RECIPE_TYPE;
    }

    @Override
    @Nullable
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public Component getTitle() {
        return Component.literal("BioFabrication - Flesh Forge");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FleshForgeRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 54,34).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 104,34).addItemStack(recipe.getResultItem(null));
    }
}
