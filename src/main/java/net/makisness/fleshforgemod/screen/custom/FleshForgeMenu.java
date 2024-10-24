package net.makisness.fleshforgemod.screen.custom;

import net.makisness.fleshforgemod.block.ModBlocks;
import net.makisness.fleshforgemod.block.entity.custom.FleshForgeBlockEntity;
import net.makisness.fleshforgemod.screen.ModMenuTypes;
import net.minecraft.core.BlockPos;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.neoforged.neoforge.items.SlotItemHandler;

import static net.makisness.fleshforgemod.block.entity.custom.FleshGeneratorBlockEntity.SLOT;

public class FleshForgeMenu extends AbstractContainerMenu {
    public final FleshForgeBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;
    private int power;
    private final BlockPos pos;


    public FleshForgeMenu(int containerId, Inventory inv, Player player, BlockPos pos) {
        super(ModMenuTypes.FLESH_FORGE_MENU.get(), containerId);
        this.level = inv.player.level();
        this.blockEntity = (FleshForgeBlockEntity) player.level().getBlockEntity(pos);

        assert this.blockEntity != null;
        this.data = this.blockEntity.getData();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);



        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler,0,45,25));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler,1,64,25));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler,2,45,44));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler,3,64,44));
        this.addSlot(new SlotItemHandler(this.blockEntity.itemHandler,4,114,34));

        addDataSlots(data);

        this.pos = pos;
        if (player.level().getBlockEntity(pos) instanceof FleshForgeBlockEntity forge) {
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return forge.getStoredPower() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    FleshForgeMenu.this.power = (FleshForgeMenu.this.power & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (forge.getStoredPower() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    FleshForgeMenu.this.power = (FleshForgeMenu.this.power & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });
        }
    }

    public int getPower() {
        return power;
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 5;  // must be the number of slots you have!

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.FLESH_FORGE.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledArrowProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int arrowPixelSize = 24;

        return  maxProgress != 0 && progress !=0 ? progress * arrowPixelSize / maxProgress : 0;
    }

}


