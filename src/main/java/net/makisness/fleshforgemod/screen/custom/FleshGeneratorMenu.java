package net.makisness.fleshforgemod.screen.custom;

import net.makisness.fleshforgemod.block.ModBlocks;
import net.makisness.fleshforgemod.block.entity.custom.FleshForgeBlockEntity;
import net.makisness.fleshforgemod.block.entity.custom.FleshGeneratorBlockEntity;
import net.makisness.fleshforgemod.screen.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

import static net.makisness.fleshforgemod.block.entity.custom.FleshGeneratorBlockEntity.SLOT;
import static net.makisness.fleshforgemod.block.entity.custom.FleshGeneratorBlockEntity.SLOT_COUNT;

public class FleshGeneratorMenu extends AbstractContainerMenu {
    private final BlockPos pos;
    private int power;

    public FleshGeneratorMenu(int containerId, Player player, BlockPos pos) {
        super(ModMenuTypes.FLESH_GENERATOR_MENU.get(),containerId);
        this.pos = pos;
        if(player.level().getBlockEntity(pos) instanceof FleshGeneratorBlockEntity fleshGeneratorBlockEntity){
          addSlot(new SlotItemHandler(fleshGeneratorBlockEntity.getItems(), SLOT, 64, 24));
          addDataSlot(new DataSlot() {
              @Override
              public int get() {
                  return fleshGeneratorBlockEntity.getStoredPower() & 0xffff;
              }

              @Override
              public void set(int value) {
                FleshGeneratorMenu.this.power = (FleshGeneratorMenu.this.power & 0xffff) | ((value & 0xffff) << 16);
              }
          });
          addDataSlot(new DataSlot() {
              @Override
              public int get() {
                  return (fleshGeneratorBlockEntity.getStoredPower() >> 16) & 0xffff;
              }

              @Override
              public void set(int value) {
                  FleshGeneratorMenu.this.power = (FleshGeneratorMenu.this.power & 0xffff) | ((value & 0xffff) << 16);
              }
          });
        }
        layoutPlayerInventorySlots(player.getInventory(), 10, 70);
    }

    public int getPower() {
        return power;
    }

    private int addSlotRange(Inventory playerInventory, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new Slot(playerInventory, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(Inventory playerInventory, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(playerInventory, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(Inventory playerInventory, int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index < SLOT_COUNT) {
                if (!this.moveItemStackTo(stack, SLOT_COUNT, Inventory.INVENTORY_SIZE + SLOT_COUNT, true)) {
                    return ItemStack.EMPTY;
                }
            }
            if (!this.moveItemStackTo(stack, SLOT, SLOT+1, false)) {
                if (index < 27 + SLOT_COUNT) {
                    if (!this.moveItemStackTo(stack, 27 + SLOT_COUNT, 36 + SLOT_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < Inventory.INVENTORY_SIZE + SLOT_COUNT && !this.moveItemStackTo(stack, SLOT_COUNT, 27 + SLOT_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return itemstack;
    }


    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(),pos),player, ModBlocks.FLESH_GENERATOR.get());
    }
}