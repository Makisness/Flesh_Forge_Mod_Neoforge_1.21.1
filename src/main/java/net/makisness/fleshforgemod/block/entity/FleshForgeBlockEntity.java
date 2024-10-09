package net.makisness.fleshforgemod.block.entity;

import net.makisness.fleshforgemod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class FleshForgeBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2);

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78; // For example, 200 ticks = 10 seconds

    public FleshForgeBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FLESH_FORGE_BE.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> FleshForgeBlockEntity.this.progress;
                    case 1 -> FleshForgeBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                return switch (index) {
                    case 0 -> FleshForgeBlockEntity.this.progress = value;
                    case 1 -> FleshForgeBlockEntity.this.maxProgress = value;
                };
            }
                @Override
                public int getCount () {
                    return 2;
                }
            };
        }
//    public void tick() {
//        if (!level.isClientSide) {
//            ItemStack inputStack = itemHandler.getStackInSlot(0);
//            ItemStack outputStack = itemHandler.getStackInSlot(1);
//
//            if (canProcess(inputStack)) {
//                progress++;
//                if (progress >= maxProgress) {
//                    processItem();
//                    progress = 0;
//                }
//            } else {
//                progress = 0;
//            }
//        }
//    }


    private boolean canProcess(ItemStack input) {
        // Define logic for valid input item
        return !input.isEmpty() && input.getItem() == ModItems.FLESH_MASS.get();
    }

    private void processItem() {
        // Define the output item logic
        itemHandler.extractItem(0, 1, false); // Remove input item
        itemHandler.insertItem(1, new ItemStack(ModItems.BIO_CPU.get()), false); // Add output item
    }


    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i <  itemHandler.getSlots(); i++){
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.fleshforgemod.flesh_forge");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return ;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory",itemHandler.serializeNBT(registries));
        tag.putInt("flesh_forge.progress",progress);

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries,tag.getCompound("inventory"));
    }

    public void tick(Level level, BlockPos pos, BlockState state){
        if(hasRecipe()){
            increaseCraftingProgress();
            setChanged(level,pos,state);

            if(hasProgressFinished()){
                craftItem();
                resetProgress();
            }
        }else {
            resetProgress();
        }
    }

    private void resetProgress() {
        progress =0;
    }

    private void craftItem() {
        ItemStack result = new ItemStack(ModItems.BIO_CPU.get(), 1);
        this.itemHandler.extractItem(INPUT_SLOT,1,false);

        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    private boolean hasRecipe() {
        boolean hasCraftingItem = this.itemHandler.getStackInSlot(INPUT_SLOT).getItem() == ModItems.FLESH_MASS.get();
        ItemStack result = new ItemStack(ModItems.BIO_CPU.get());

        return hasCraftingItem && canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean hasProgressFinished() {
        return progress>=maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }


}
