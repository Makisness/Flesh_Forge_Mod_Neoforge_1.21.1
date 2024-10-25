package net.makisness.fleshforgemod.block.entity.custom;

import net.makisness.fleshforgemod.block.custom.FleshForgeBlock;
import net.makisness.fleshforgemod.block.entity.ModBlockEntities;
import net.makisness.fleshforgemod.component.ModDataComponentTypes;
import net.makisness.fleshforgemod.fleshforgemod;
import net.makisness.fleshforgemod.item.ModItems;
import net.makisness.fleshforgemod.recipe.FleshForgeRecipe;
import net.makisness.fleshforgemod.recipe.FleshForgeRecipeInput;
import net.makisness.fleshforgemod.recipe.ModRecipes;
import net.makisness.fleshforgemod.screen.custom.FleshForgeMenu;
import net.makisness.fleshforgemod.tools.AdaptedEnergyStorage;
import net.makisness.fleshforgemod.tools.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Supplier;

public class FleshForgeBlockEntity extends BlockEntity implements MenuProvider {

    public final ItemStackHandler itemHandler = new ItemStackHandler(5){
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide){
                level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(),3);
            }
        }
    };

    //private static final int FLUID_ITEM_SLOT = ;
    private static final int INPUT_SLOT = 0;
    private static final int INPUT_SLOT_2 = 1;
    private static final int INPUT_SLOT_3 = 2;
    private static final int INPUT_SLOT_4 = 3;
    private static final int OUTPUT_SLOT = 4;

    ItemStack[] inputs = {
            itemHandler.getStackInSlot(INPUT_SLOT),
            itemHandler.getStackInSlot(INPUT_SLOT_2),
            itemHandler.getStackInSlot(INPUT_SLOT_3),
            itemHandler.getStackInSlot(INPUT_SLOT_4)
    };


    public static final int MAX_TRANSFER = 1000;
    public static final int CAPACITY = 100000;
    public static final int ENERGY = 0;

    private static int energyUseModifier = 1;
    private static int energyPerTick = 100 * energyUseModifier;

    private final ContainerData data;
    private int progress;
    private int maxProgress = 72;
    private final int DEFAULT_MAX_PROGRESS = 72;

    private final CustomEnergyStorage energy = createEnergyStorage();
    private final Lazy<IEnergyStorage> energyHandler = Lazy.of(() -> new AdaptedEnergyStorage(energy){
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            setChanged();
            return super.receiveEnergy(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    });

    public FleshForgeBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FLESH_FORGE_BE.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index){
                    case 0 -> FleshForgeBlockEntity.this.progress;
                    case 1 -> FleshForgeBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
            switch(index) {
                case 0 -> FleshForgeBlockEntity.this.progress = value;
                case 1 -> FleshForgeBlockEntity.this.maxProgress = value;
                };
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("blockEntity.fleshforgemod.flesh_forge");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FleshForgeMenu(containerId, playerInventory, player, getBlockPos());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if(energy.getEnergyStored() != 0 ) {
            components.set(ModDataComponentTypes.ENERGY.get(), this.energy.getEnergyStored());
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.energy.setEnergyStorage(componentInput.getOrDefault(ModDataComponentTypes.ENERGY.get(),0));
    }



    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("flesh_forge.progress",progress);
        tag.putInt("flesh_forge.max_progress", maxProgress);
        tag.putInt("flesh_forge.energy", energy.getEnergyStored());

        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("flesh_forge.progress");
        maxProgress = tag.getInt("flesh_forge.max_progress");
        energy.setEnergyStorage(tag.getInt("flesh_forge.energy"));

        itemHandler.deserializeNBT(registries,tag.getCompound("inventory"));
    }

    public void drops(){
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++){
            inv.setItem(i,itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    public void tick(Level level, BlockPos pos, BlockState state){
        if(hasRecipe() && isOutputSlotEmptyOrReceivable() && hasEnergy()){

            level.setBlock(pos, state.setValue(FleshForgeBlock.ACTIVE, true), 3);
            increaseCraftingProgress();
            depletePower();
            setChanged(level,pos,state);
            if(hasCraftingFinished()){
                craftItem();
                resetProgress();
                level.setBlock(pos, state.setValue(FleshForgeBlock.ACTIVE, false), 3);
            }
        } else {
            resetProgress();
            level.setBlock(pos, state.setValue(FleshForgeBlock.ACTIVE, false), 3);
        }
    }

    private boolean hasEnergy() {
       return this.energy.getEnergyStored() >= energyPerTick;
    }

    private void depletePower() {
        this.energy.setEnergyStorage(energy.getEnergyStored()- energyPerTick);
    }


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
        Optional<RecipeHolder<FleshForgeRecipe>> recipe = getCurrentRecipe();
        ItemStack output = recipe.get().value().output();

        itemHandler.extractItem(INPUT_SLOT, 1, false);
        itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(output.getItem(),
                itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + output.getCount()));
    }

    private boolean hasCraftingFinished() {
        return this.progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() < this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<FleshForgeRecipe>> recipe = getCurrentRecipe();
        if(recipe.isEmpty()){
            return false;
        }
        ItemStack output = recipe.get().value().output();
        maxProgress = recipe.get().value().cookTime();
        return canInsertAmountIntoOutputSlot(output.getCount()) && canInsertItemIntoOutputSlot(output);
    }

    private Optional<RecipeHolder<FleshForgeRecipe>> getCurrentRecipe() {
        FleshForgeRecipeInput recipeInput = new FleshForgeRecipeInput(
                itemHandler.getStackInSlot(INPUT_SLOT),
                itemHandler.getStackInSlot(INPUT_SLOT_2),
                itemHandler.getStackInSlot(INPUT_SLOT_3),
                itemHandler.getStackInSlot(INPUT_SLOT_4)
        );


        return this.level.getRecipeManager()
                .getRecipeFor(ModRecipes.FLESHFORGE_TYPE.get(),recipeInput, level);
    }
    private int getCookTime() {
        return getCurrentRecipe().get().value().cookTime();
    }

    private boolean canInsertItemIntoOutputSlot(ItemStack output) {
        return itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ||
                itemHandler.getStackInSlot(OUTPUT_SLOT).getItem() == output.getItem();
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
       int maxCount = itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() ? 64 : itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
       int currentCount = itemHandler.getStackInSlot(OUTPUT_SLOT).getCount();

       return maxCount >= currentCount + count;
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

    @NotNull
    private CustomEnergyStorage createEnergyStorage(){
        return new CustomEnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER,ENERGY);
    }
    public IEnergyStorage getEnergyHandler(){
        return energyHandler.get();
    }

    public int getStoredPower(){
        return energy.getEnergyStored();
    }

    public int getMaxStoredPower(){
        return energy.getMaxEnergyStored();
    }

    public ContainerData getData() {
        return this.data;
    }
    public ItemStackHandler getItems(){
        return itemHandler;
    }

    public IItemHandler getItemHandler(){
        return itemHandler;
    }

}

