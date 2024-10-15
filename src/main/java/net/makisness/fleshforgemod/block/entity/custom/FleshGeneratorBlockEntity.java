package net.makisness.fleshforgemod.block.entity.custom;

import net.makisness.fleshforgemod.block.ModBlocks;
import net.makisness.fleshforgemod.block.entity.ModBlockEntities;
import net.makisness.fleshforgemod.component.ModDataComponentTypes;
import net.makisness.fleshforgemod.fleshforgemod;
import net.makisness.fleshforgemod.screen.custom.FleshGeneratorMenu;
import net.makisness.fleshforgemod.tools.AdaptedEnergyStorage;
import net.makisness.fleshforgemod.tools.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snownee.jade.addon.universal.EnergyStorageProvider;

public class FleshGeneratorBlockEntity extends BlockEntity implements MenuProvider {

    public static final String ITEMS_TAG = "Inventory";
    public static final String ENERGY_TAG = "Energy";

    public static final int GENERATE = 50;
    public static final int MAX_TRANSFER = 1000;
    public static final int CAPACITY = 100000;
    public static final int ENERGY = 0;


    private static final int FUEL_ENERGY_BALANCE = 15;

    public static int SLOT_COUNT = 1;
    public static int SLOT = 0;

    public final ItemStackHandler items = createItemHandler();
    public final Lazy<IItemHandler> itemHandler = Lazy.of(() -> items);

    private final CustomEnergyStorage energy = createEnergyStorage();
    private final Lazy<IEnergyStorage> energyHandler = Lazy.of(() -> new AdaptedEnergyStorage(energy){
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
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
            return false;
        }
    });

    private int burnTime;

    public FleshGeneratorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.FLESH_GENERATOR_BE.get(), pos, blockState);
    }


    public void tickServer(Level level, BlockPos pos, BlockState state){
        generateEnergy();
        distributeEnergy();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(
                "blockEntity.fleshforgemod.flesh_generator");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FleshGeneratorMenu(containerId, player, this.getBlockPos());
    }

    private void generateEnergy(){
        if(energy.getEnergyStored() < energy.getMaxEnergyStored()){
            if(burnTime <= 0){
                ItemStack fuel = items.getStackInSlot(SLOT);
                if(fuel.isEmpty()){
                    //No fuel
                    return;
                }
                setBurnTime(foodToFuel(items.getStackInSlot(SLOT)));
                if(burnTime <= 0){
                    //Not a food
                    return;
                }
                items.extractItem(SLOT,1,false);
            }else{
                setBurnTime(burnTime - 1);
                energy.receiveEnergy(GENERATE,false);
            }
            setChanged();
        }
    }

    public static int foodToFuel(ItemStack itemStack){
        float foodBurnTime = 0;
        if(isFood(itemStack)) {
            if (hasNutrition(itemStack)) {
                if (hasSaturation(itemStack)) {
                    int fuelNutrition = itemStack.getFoodProperties(null).nutrition();
                    float fuelSaturation = itemStack.getFoodProperties(null).saturation();
                    foodBurnTime = fuelNutrition * diminishingReturns(fuelSaturation,5) * FUEL_ENERGY_BALANCE;
                    return ((int) foodBurnTime);
                }
            }
        }else{
            return 0;
        }
        return ((int) foodBurnTime);
    }

    private static float diminishingReturns(float fuelSaturation, int weight) {
        if(fuelSaturation<1) {
            fuelSaturation = 1;
        }
        if(fuelSaturation <= weight){
            return fuelSaturation;
        }else{
            return ((float) (weight + Math.log(fuelSaturation - weight + 1)));
        }
    }


    private static boolean hasSaturation(ItemStack itemStack) {
        return itemStack.getFoodProperties(null).saturation() != 0;
    }

    private static boolean hasNutrition(ItemStack itemStack) {
        return itemStack.getFoodProperties(null).nutrition() != 0;
    }

    private static boolean isFood(ItemStack itemStack) {
        return itemStack.getFoodProperties(null)!= null;
    }


    private void setBurnTime(int bt){
        if(bt == burnTime){
            return;
        }
        burnTime = bt;
        if(getBlockState().getValue(BlockStateProperties.POWERED) != burnTime > 0) {
            level.setBlockAndUpdate(getBlockPos(),getBlockState().setValue(BlockStateProperties.POWERED, burnTime > 0));
        }
        setChanged();

    }

//    private void distributeEnergy(){
//        for(Direction direction : Direction.values()){
//            if(energy.getEnergyStored() <= 0){
//                return;
//            }

//            IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK,getBlockPos().relative(direction),null);
//
//
//            System.out.println("Block: " + level.getBlockState(getBlockPos().relative(direction)).getBlock().getName().getString());
//            System.out.println("Can Accept Energy: " + energyStorage.canReceive());
//            System.out.println("Has Energy: " + energyStorage.getEnergyStored());
//            assert level != null;
//            IEnergyStorage energy = level.getCapability(Capabilities.EnergyStorage.BLOCK, getBlockPos().relative(direction),null);
//            System.out.println("NULL ENERGY:  " + energy);
//            if(energy != null && energy.canReceive()){
//                    System.out.println("CAN RECIEVE ENERGER");
//                    int received = energy.receiveEnergy(Math.min(this.energy.getEnergyStored(),MAX_TRANSFER),false);
//                    this.energy.extractEnergy(received,false);
//                    System.out.println("Energy Received by Neighbor: " + received);
   //                 setChanged();
//            }
//
//}
//    private void distributeEnergy() {
//        // Check all sides of the block and send energy if that block supports the energy capability
//        for (Direction direction : Direction.values()) {
//    //            if (energy.getEnergyStored() <= 0) {
//    //                return;
//    //            }
//            System.out.println("Direction: " + direction + " | Block: " + level.getBlockState(getBlockPos().relative(direction)).getBlock().getName().getString());
//            System.out.println("Direction: " + direction + " | Entity: " + level.getBlockEntity(getBlockPos().relative(direction)));
//            System.out.println("Direction: " + direction + " | Energy: " + level.getCapability(Capabilities.EnergyStorage.BLOCK,getBlockPos().relative(direction),level.getBlockState(getBlockPos().relative(direction)),level.getBlockEntity(getBlockPos().relative(direction)),null));
//
//        }
//    }


    public void distributeEnergy() {
        for (Direction direction : Direction.values()) {
            if (energy.getEnergyStored() <= 0) {
                System.out.println("No energy to distribute.");
                return;
            }
            BlockEntity adjacentEntity = level.getBlockEntity(getBlockPos().relative(direction));
            System.out.println("Checking adjacent block at direction: " + direction + " | Block Entity: " + adjacentEntity);
            if (adjacentEntity != null) {
                IEnergyStorage energy = level.getCapability(Capabilities.EnergyStorage.BLOCK, getBlockPos().relative(direction),null);
                if (energy != null) {
                    System.out.println("Energy capability found for adjacent block at direction: " + direction);
                    if (energy.canReceive()) {
                        int energyTransferred = energy.receiveEnergy(Math.min(energy.getEnergyStored(), 1000), false);
                        energy.extractEnergy(energyTransferred, false);
                        setChanged();
                        System.out.println("Transferred " + energyTransferred + " energy to adjacent block at direction: " + direction);
                    }
                } else {
                    System.out.println("No energy capability found for adjacent block at direction: " + direction);
                }
            }
        }
    }


    public ItemStackHandler getItems(){
        return items;
    }

    public int getStoredPower(){
        return energy.getEnergyStored();
    }

    public int getMaxStoredPower(){
        return energy.getMaxEnergyStored();
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
        tag.put(ITEMS_TAG, items.serializeNBT(registries));
        tag.putInt(ENERGY_TAG, energy.getEnergyStored());
        tag.putInt("BurnTimeLeft", burnTime);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if(tag.contains(ITEMS_TAG)){
            items.deserializeNBT(registries,tag.getCompound(ITEMS_TAG));
        }
        if (tag.contains(ENERGY_TAG)) {
           //energy.deserializeNBT(registries,tag.get(ENERGY_TAG));
            energy.setEnergyStorage(tag.getInt(ENERGY_TAG));
           burnTime = tag.getInt("BurnTimeLeft");
        }
    }
    public void drops(){
        SimpleContainer inv = new SimpleContainer(items.getSlots());
        for(int i = 0; i < items.getSlots(); i++){
            inv.setItem(i,items.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }



    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    private ItemStackHandler createItemHandler(){
        return new ItemStackHandler(SLOT_COUNT){
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if(!level.isClientSide){
                    level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(),3);
                }
            }
        };
    }
    @NotNull
    private CustomEnergyStorage createEnergyStorage(){
        return new CustomEnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER,ENERGY);
    }
    public IItemHandler getItemHandler(){
        return itemHandler.get();
    }

    public IEnergyStorage getEnergyHandler(){
        return energyHandler.get();
    }

    public int getBurnTimePassed() {
        return burnTime - getMaxBurnTime();
    }

    public int getMaxBurnTime() {
        return foodToFuel(items.getStackInSlot(SLOT));
    }
    public boolean isInventoryEmpty() {
        return items.getStackInSlot(0).isEmpty();
    }

    public ItemStack getItem(int slot) {
        return items.getStackInSlot(slot);
    }

    public static final BlockCapability<IEnergyStorage, @Nullable Direction> BLOCK = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID, "energy_handler"), IEnergyStorage.class);
    public static final EntityCapability<IEnergyStorage, @Nullable Direction> ENTITY = EntityCapability.createSided(ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID, "energy_handler"), IEnergyStorage.class);
}

