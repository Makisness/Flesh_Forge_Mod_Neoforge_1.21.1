package net.makisness.fleshforgemod.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record FleshForgeRecipe(Ingredient inputItem, ItemStack output, int cookTime) implements Recipe<FleshForgeRecipeInput> {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(inputItem);
        return list;
    }

    @Override
    public boolean matches(FleshForgeRecipeInput input, Level level) {
        if(level.isClientSide()) {
            return false;
        }

        return inputItem.test(input.getItem(0));
    }

    @Override
    public ItemStack assemble(FleshForgeRecipeInput pInput, HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.FLESHFORGE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.FLESHFORGE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<FleshForgeRecipe> {
        public static final MapCodec<FleshForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(FleshForgeRecipe::inputItem),
                ItemStack.CODEC.fieldOf("result").forGetter(FleshForgeRecipe::output),
                Codec.INT.fieldOf("cookTime").forGetter(FleshForgeRecipe::cookTime)
        ).apply(inst, FleshForgeRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FleshForgeRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        Ingredient.CONTENTS_STREAM_CODEC, FleshForgeRecipe::inputItem,
                        ItemStack.STREAM_CODEC, FleshForgeRecipe::output,
                        ByteBufCodecs.INT,FleshForgeRecipe::cookTime,
                        FleshForgeRecipe::new);

        @Override
        public MapCodec<FleshForgeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FleshForgeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}