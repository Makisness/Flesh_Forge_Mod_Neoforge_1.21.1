package net.makisness.fleshforgemod.recipe;

import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

import static net.minecraft.data.recipes.SpecialRecipeBuilder.special;

public class ModRecipes {
        public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
                DeferredRegister.create(Registries.RECIPE_SERIALIZER, fleshforgemod.MODID);
        public static final DeferredRegister<RecipeType<?>> TYPES =
                DeferredRegister.create(Registries.RECIPE_TYPE, fleshforgemod.MODID);

        public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FleshForgeRecipe>> FLESHFORGE_SERIALIZER =
                SERIALIZERS.register("biofabricating", FleshForgeRecipe.Serializer::new);

        public static final DeferredHolder<RecipeType<?>, RecipeType<FleshForgeRecipe>> FLESHFORGE_TYPE =
                TYPES.register("biofabricating", () -> new RecipeType<FleshForgeRecipe>() {
                    @Override
                    public String toString() {
                        return "biofabricating";
                    }
                });

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<OrganSorterRecipe>> ORGAN_SORTER_SERIALIZER =
            SERIALIZERS.register("organ_sorting", OrganSorterSerializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<OrganSorterRecipe>> ORGAN_SORTER_TYPE =
            TYPES.register("organ_sorting", () -> new RecipeType<OrganSorterRecipe>() {
                @Override
                public String toString() {
                    return "organ_sorting";
                }
            });

        public static final DeferredHolder<RecipeSerializer<?>, ShapelessBucketRecipe.Serializer> SHAPELESS_BUCKET_SERIALIZER =
            SERIALIZERS.register("shapelessbucket", ShapelessBucketRecipe.Serializer::new);

    public static final Supplier<RecipeType<ShapelessBucketRecipe>> SHAPELESS_BUCKET_TYPE =
            TYPES.register(
                    "shapelessbucket",
                    // We need the qualifying generic here due to generics being generics.
                    () -> RecipeType.<ShapelessBucketRecipe>simple(ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID, "shapelessbucket")));

        public static void register(IEventBus eventBus) {
            SERIALIZERS.register(eventBus);
            TYPES.register(eventBus);
        }
    }

