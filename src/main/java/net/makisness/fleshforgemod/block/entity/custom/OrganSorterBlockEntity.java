package net.makisness.fleshforgemod.block.entity.custom;

import net.makisness.fleshforgemod.block.entity.ModBlockEntities;
import net.makisness.fleshforgemod.component.ModDataComponentTypes;
import net.makisness.fleshforgemod.recipe.FleshForgeRecipe;
import net.makisness.fleshforgemod.recipe.FleshForgeRecipeInput;
import net.makisness.fleshforgemod.recipe.ModRecipes;
import net.makisness.fleshforgemod.recipe.OrganSorterRecipe;
import net.makisness.fleshforgemod.screen.custom.FleshForgeMenu;
import net.makisness.fleshforgemod.screen.custom.OrganSorterMenu;
import net.makisness.fleshforgemod.tools.AdaptedEnergyStorage;
import net.makisness.fleshforgemod.tools.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class OrganSorterBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler itemHandler = new ItemStackHandler(10){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide){
                level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(),3);
            }
        }
    };

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    private static final int OUTPUT_SLOT_2 = 2;
    private static final int OUTPUT_SLOT_3 = 3;
    private static final int OUTPUT_SLOT_4 = 4;
    private static final int OUTPUT_SLOT_5 = 5;
    private static final int OUTPUT_SLOT_6 = 6;
    private static final int OUTPUT_SLOT_7 = 7;
    private static final int OUTPUT_SLOT_8 = 8;
    private static final int OUTPUT_SLOT_9 = 9;


    private final ContainerData data;
    private static int progress;
    private int maxProgress = 72;
    private final int DEFAULT_MAX_PROGRESS = 72;


    public OrganSorterBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.ORGAN_SORTER_BE.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> OrganSorterBlockEntity.this.progress;
                    case 1 -> OrganSorterBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
            switch(index) {
                case 0 -> OrganSorterBlockEntity.this.progress = value;
                case 1 -> OrganSorterBlockEntity.this.maxProgress = value;
                };
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("blockEntity.fleshforgemod.organ_sorter");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
        return new OrganSorterMenu(containerId, inv, player, getBlockPos());
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("organ_sorter.progress",progress);
        tag.putInt("organ_sorter.max_progress", maxProgress);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries,tag.getCompound("inventory"));
        progress = tag.getInt("organ_sorter.progress");
        maxProgress = tag.getInt("organ_sorter.max_progress");
    }

    public void drops(){
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++){
            inv.setItem(i,itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }


    public void handleManualCraftingProgress(Level level, BlockPos pos, BlockState state) {
        if (hasRecipe() && isOutputSlotEmptyOrReceivable()) {
            increaseCraftingProgress(); // Increment the crafting progress
            setChanged(level, pos, state); // Ensure the state change is marked and synced
            if (hasCraftingFinished()) {
                craftItem(); // Perform crafting
                resetProgress(); // Reset progress after crafting
            }
        } else {
            resetProgress(); // Reset progress if recipe is invalid
        }
    }

//    public void tick(Level level, BlockPos pos, BlockState state){
//        if(hasRecipe() && isOutputSlotEmptyOrReceivable()){
//            increaseCraftingProgress();
//            setChanged(level,pos,state);
//            if(hasCraftingFinished()){
//                craftItem();
//                resetProgress();
//            }
//        } else {
//            resetProgress();
//        }
//    }

    public int getProgress() {
       return progress;
    }
    public int getMaxProgress() {
        return maxProgress;
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = DEFAULT_MAX_PROGRESS;
    }



    private void craftItem() {
        Optional<RecipeHolder<OrganSorterRecipe>> recipe = getCurrentRecipe();

        if (recipe.isPresent()) {
            ItemStack recipeOut = BuiltInRegistries.ITEM.get(recipe.get().value().getWeightedRandomOutput()).getDefaultInstance();
            ItemStack output = recipeOut.copy(); // Create a fresh copy of the output

            // Extract the input item from the input slot
            itemHandler.extractItem(INPUT_SLOT, 1, false);

            // Find an available output slot
            int availableSlot = findAvailableOutputSlot(output);

            if (availableSlot != -1) {
                ItemStack currentStack = itemHandler.getStackInSlot(availableSlot);

                // If the slot is empty, place the output there
                if (currentStack.isEmpty()) {
                    itemHandler.setStackInSlot(availableSlot, output);
                }
                // If the slot already contains the same item, add to the existing stack
                else {
                    int newCount = currentStack.getCount() + output.getCount();
                    int maxStackSize = currentStack.getMaxStackSize();

                    if (newCount <= maxStackSize) {
                        currentStack.setCount(newCount);
                    } else {
                        currentStack.setCount(maxStackSize);
                        output.setCount(newCount - maxStackSize);
                        // If there's leftover, try placing it in another slot
                        availableSlot = findAvailableOutputSlot(output);
                        if (availableSlot != -1) {
                            itemHandler.setStackInSlot(availableSlot, output);
                        }
                    }
                    itemHandler.setStackInSlot(availableSlot, currentStack);
                }
            }
        }
    }

    private boolean hasCraftingFinished() {
        return this.progress >= maxProgress;
    }

    public static void increaseCraftingProgress() {
        progress++;
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() < this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<OrganSorterRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty()){
            return false;
        }

        ItemStack recipeOut = BuiltInRegistries.ITEM.get(recipe.get().value().getWeightedRandomOutput()).getDefaultInstance();

        ItemStack output = recipeOut;
        maxProgress = recipe.get().value().rightClickCount();
        return canInsertAmountIntoOutputSlot(output,output.getCount()) && canInsertItemIntoOutputSlot(output);
    }

    private Optional<RecipeHolder<OrganSorterRecipe>> getCurrentRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.ORGAN_SORTER_TYPE.get(), new RecipeInput() {
                    @Override
                    public ItemStack getItem(int index) {
                        return itemHandler.getStackInSlot(INPUT_SLOT);
                    }

                    @Override
                    public int size() {
                        return 1;
                    }
                }, level);
    }
    private int getCookTime() {
        return getCurrentRecipe().get().value().getRightClickCount();
    }


    private int findAvailableOutputSlot(ItemStack output) {
        for (int i = OUTPUT_SLOT; i <= OUTPUT_SLOT_9; i++) {
            ItemStack slotStack = itemHandler.getStackInSlot(i);
            if (slotStack.isEmpty() || (slotStack.getItem() == output.getItem() && slotStack.getCount() < slotStack.getMaxStackSize())) {
                return i; // Return the index of the available slot
            }
        }
        return -1; // Return -1 if no suitable slot is found
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return findAvailableOutputSlot(output) != -1;
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack output, int count) {
        int availableSlot = findAvailableOutputSlot(output);
        if (availableSlot == -1) {
            return false; // No available slot
        }

        ItemStack slotStack = itemHandler.getStackInSlot(availableSlot);
        int maxCount = slotStack.isEmpty() ? 64 : slotStack.getMaxStackSize();
        int currentCount = slotStack.getCount();

        return currentCount + count <= maxCount;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }


    public boolean isInventoryEmpty() {
        // Loop through all slots in the item handler
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            // Check if any slot contains a non-empty stack
            if (!itemHandler.getStackInSlot(slot).isEmpty()) {
                return false; // Return false if any slot is not empty
            }
        }
        return true; // Return true if all slots are empty
    }

    public ItemStack getItem(int slot) {
        return itemHandler.getStackInSlot(slot);
    }

    public ContainerData getData() {
       return this.data;
    }

    public IItemHandler getItemHandler(){
        return itemHandler;
    }
}

