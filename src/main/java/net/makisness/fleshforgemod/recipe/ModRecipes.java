package net.makisness.fleshforgemod.recipe;

import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;

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

        public static void register(IEventBus eventBus) {
            SERIALIZERS.register(eventBus);
            TYPES.register(eventBus);
        }
    }

