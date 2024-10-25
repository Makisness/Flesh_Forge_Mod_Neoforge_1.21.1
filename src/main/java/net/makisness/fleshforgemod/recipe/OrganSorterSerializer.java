package net.makisness.fleshforgemod.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.HashMap;
import java.util.Map;

public class OrganSorterSerializer implements RecipeSerializer<OrganSorterRecipe> {
    // Codec for the Map of ItemStack to INT (representing probability)
    public static final UnboundedMapCodec<ResourceLocation, Integer> ITEMSTACK_WEIGHT_MAP_CODEC = Codec.unboundedMap(
            ResourceLocation.CODEC,
            Codec.INT
    );
    public static final StreamCodec<FriendlyByteBuf, Map<ResourceLocation, Integer>> ITEMSTACK_WEIGHT_MAP_STREAM_CODEC = new StreamCodec<>() {

        @Override
        public void encode(FriendlyByteBuf buffer, Map<ResourceLocation, Integer> value) {
            // Write the size of the map
            buffer.writeVarInt(value.size());

            // Write each ItemStack and its corresponding weight
            for (Map.Entry<ResourceLocation, Integer> entry : value.entrySet()) {
                buffer.writeUtf(String.valueOf(entry.getKey()));   // Write the ItemStack manually
                buffer.writeVarInt(entry.getValue());     // Write the corresponding weight
            }
        }

        @Override
        public Map<ResourceLocation, Integer> decode(FriendlyByteBuf buffer) {
            // Read the size of the map
            int size = buffer.readVarInt();
            Map<ResourceLocation, Integer> map = new HashMap<>(size);

            // Read each ItemStack and its corresponding weight
            for (int i = 0; i < size; i++) {
                ResourceLocation resourceLocation = buffer.readResourceLocation();
                int weight = buffer.readVarInt();             // Read the corresponding weight
                map.put(resourceLocation, weight);
            }

            return map;
        }

    };


    // Define the MapCodec for the ProbabilisticRecipe
    public static final MapCodec<OrganSorterRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ItemStack.CODEC.fieldOf("input").forGetter(OrganSorterRecipe::inputItem),
            ITEMSTACK_WEIGHT_MAP_CODEC.fieldOf("outputProbabilities").forGetter(OrganSorterRecipe::getOutputWithWeights),
            Codec.INT.fieldOf("rightClickCount").forGetter(OrganSorterRecipe::getRightClickCount)
    ).apply(inst, OrganSorterRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, OrganSorterRecipe> ORGAN_SORTER_RECIPE_STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, OrganSorterRecipe::inputItem,                       // Codec for input ItemStack
            ITEMSTACK_WEIGHT_MAP_STREAM_CODEC, OrganSorterRecipe::outputs,               // StreamCodec for Map<ItemStack, Integer>
            ByteBufCodecs.INT, OrganSorterRecipe::rightClickCount,                       // Codec for rightClickCount
            OrganSorterRecipe::new);  // Constructor


    @Override
    public MapCodec<OrganSorterRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, OrganSorterRecipe> streamCodec() {
        return ORGAN_SORTER_RECIPE_STREAM_CODEC;
    }
}

