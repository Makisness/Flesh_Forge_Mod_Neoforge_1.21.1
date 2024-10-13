package net.makisness.fleshforgemod.block.entity.custom;

import net.makisness.fleshforgemod.block.entity.ModBlockEntities;
import net.makisness.fleshforgemod.component.ModDataComponentTypes;
import net.makisness.fleshforgemod.item.ModItems;
import net.makisness.fleshforgemod.screen.custom.FleshForgeMenu;
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
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                    System.out.println(itemStack.getFoodProperties(null).nutrition()+" : Nutrition");
                    float fuelSaturation = itemStack.getFoodProperties(null).saturation();
                    System.out.println(itemStack.getFoodProperties(null).saturation()+" : Saturation");
                    foodBurnTime = fuelNutrition * diminishingReturns(fuelSaturation,5) * FUEL_ENERGY_BALANCE;
                    System.out.println("NEW SATURATION: " + diminishingReturns(fuelSaturation,5));
                    System.out.println("FOOD TO FUEL - BURN TIME: " + ((int) foodBurnTime) + "- FOOD: " + itemStack.getDisplayName().getString());
                    return ((int) foodBurnTime);
                }
            }
        }else{
        System.out.println("NOT A FOOD");
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
        System.out.println("Has Saturation");
        return itemStack.getFoodProperties(null).saturation() != 0;
    }

    private static boolean hasNutrition(ItemStack itemStack) {
        System.out.println("Has Nutrition");
        return itemStack.getFoodProperties(null).nutrition() != 0;
    }

    private static boolean isFood(ItemStack itemStack) {
        System.out.println("Food Added");
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

    private void distributeEnergy(){
        for(Direction direction : Direction.values()){
            if(energy.getEnergyStored()<-0){
                return;
            }
            IEnergyStorage energy = level.getCapability(Capabilities.EnergyStorage.BLOCK,getBlockPos().relative(direction),null);
            if(energy != null){
                if(energy.canReceive()){
                    int received = energy.receiveEnergy(Math.min(this.energy.getEnergyStored(),MAX_TRANSFER),false);
                    this.energy.extractEnergy(received,false);
                    setChanged();
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
}

