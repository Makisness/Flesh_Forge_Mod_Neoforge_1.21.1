package net.makisness.fleshforgemod.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

    public class FleshForgeRecipeInput implements RecipeInput {
        private final List<ItemStack> inputs;

        public FleshForgeRecipeInput(ItemStack... inputs) {
            this.inputs = Arrays.stream(inputs)
                    .filter(stack -> !stack.isEmpty()) // Only include non-empty stacks
                    .collect(Collectors.toList());
        }



        public List<ItemStack> getInputs() {
            return inputs;
        }

        @Override
        public ItemStack getItem(int index) {
            if (index < 0 || index >= inputs.size()) {
                return ItemStack.EMPTY;
            }
            return inputs.get(index);
        }

        @Override
        public int size() {
            return inputs.size(); // Returns the number of non-empty input stacks
        }
    }
