package net.makisness.fleshforgemod.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public record FleshForgeRecipe(List<Ingredient> inputItems, ItemStack output, int cookTime) implements Recipe<FleshForgeRecipeInput> {

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.addAll(inputItems);
        return list;
    }

    @Override
    public boolean matches(FleshForgeRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }

        // Get the list of input items from the recipe input
        List<ItemStack> inputs = input.getInputs();

        // Ensure the input count is not less than the number of required ingredients
        if (inputs.size() < inputItems.size()) {
            return false; // Not enough items to match
        }

        // Create a mutable list of remaining ingredients to match against
        List<Ingredient> remainingIngredients = new ArrayList<>(inputItems);

        // Iterate over each input stack to find a match
        for (ItemStack inputStack : inputs) {
            boolean matched = false;

            // Iterate over the remaining ingredients to find a match for this input
            Iterator<Ingredient> iterator = remainingIngredients.iterator();
            while (iterator.hasNext()) {
                Ingredient ingredient = iterator.next();
                if (ingredient.test(inputStack)) {
                    iterator.remove(); // Remove matched ingredient
                    matched = true;
                    break;
                }
            }

            // If the input could not be matched to any remaining ingredient, return false
            if (!matched) {
                return false;
            }
        }

        // If all ingredients have been matched, return true
        return remainingIngredients.isEmpty();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(FleshForgeRecipeInput input) {
        return Recipe.super.getRemainingItems(input);
    }

    @Override
    public ItemStack assemble(FleshForgeRecipeInput input, HolderLookup.Provider registries) {
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

    public static final StreamCodec<RegistryFriendlyByteBuf, List<Ingredient>> LIST_STREAM_CODEC =
            Ingredient.CONTENTS_STREAM_CODEC.apply(
                    // The list can only have up to 256 elements
                    ByteBufCodecs.list(4)
            );

    // Recipe serializer class for handling serialization and deserialization
    public static class Serializer implements RecipeSerializer<FleshForgeRecipe> {
        public static final MapCodec<FleshForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(FleshForgeRecipe::inputItems),
                ItemStack.CODEC.fieldOf("result").forGetter(FleshForgeRecipe::output),
                Codec.INT.fieldOf("cookTime").forGetter(FleshForgeRecipe::cookTime)
        ).apply(inst, FleshForgeRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FleshForgeRecipe> STREAM_CODEC =
                StreamCodec.composite(

                        LIST_STREAM_CODEC, FleshForgeRecipe::inputItems,
                        ItemStack.STREAM_CODEC, FleshForgeRecipe::output,
                        ByteBufCodecs.INT, FleshForgeRecipe::cookTime,
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


//public record FleshForgeRecipe(Ingredient inputItem, ItemStack output, int cookTime) implements Recipe<FleshForgeRecipeInput> {
//    @Override
//    public NonNullList<Ingredient> getIngredients() {
//        NonNullList<Ingredient> list = NonNullList.create();
//        list.add(inputItem);
//        return list;
//    }
//
//    @Override
//    public boolean matches(FleshForgeRecipeInput input, Level level) {
//        if(level.isClientSide()) {
//            return false;
//        }
//
//        return inputItem.test(input.getItem(0));
//    }
//
//    @Override
//    public NonNullList<ItemStack> getRemainingItems(FleshForgeRecipeInput input) {
//        return Recipe.super.getRemainingItems(input);
//    }
//
//    @Override
//    public ItemStack assemble(FleshForgeRecipeInput pInput, HolderLookup.Provider registries) {
//        return output.copy();
//    }
//
//    @Override
//    public boolean canCraftInDimensions(int width, int height) {
//        return true;
//    }
//
//    @Override
//    public ItemStack getResultItem(HolderLookup.Provider registries) {
//        return output;
//    }
//
//    @Override
//    public RecipeSerializer<?> getSerializer() {
//        return ModRecipes.FLESHFORGE_SERIALIZER.get();
//    }
//
//    @Override
//    public RecipeType<?> getType() {
//        return ModRecipes.FLESHFORGE_TYPE.get();
//    }
//
//    public static class Serializer implements RecipeSerializer<FleshForgeRecipe> {
//        public static final MapCodec<FleshForgeRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
//                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(FleshForgeRecipe::inputItem),
//                ItemStack.CODEC.fieldOf("result").forGetter(FleshForgeRecipe::output),
//                Codec.INT.fieldOf("cookTime").forGetter(FleshForgeRecipe::cookTime)
//        ).apply(inst, FleshForgeRecipe::new));
//
//        public static final StreamCodec<RegistryFriendlyByteBuf, FleshForgeRecipe> STREAM_CODEC =
//                StreamCodec.composite(
//                        Ingredient.CONTENTS_STREAM_CODEC, FleshForgeRecipe::inputItem,
//                        ItemStack.STREAM_CODEC, FleshForgeRecipe::output,
//                        ByteBufCodecs.INT,FleshForgeRecipe::cookTime,
//                        FleshForgeRecipe::new);
//
//        @Override
//        public MapCodec<FleshForgeRecipe> codec() {
//            return CODEC;
//        }
//
//        @Override
//        public StreamCodec<RegistryFriendlyByteBuf, FleshForgeRecipe> streamCodec() {
//            return STREAM_CODEC;
//        }
//    }
//}