package net.makisness.fleshforgemod.item.custom;

import net.makisness.fleshforgemod.item.ModItems;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.property.Properties;

import java.util.List;
import java.util.Objects;

public class roundKnife extends Item {
    public roundKnife(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public void onCraftedPostProcess(ItemStack stack, Level level) {
        super.onCraftedPostProcess(stack, level);
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
            ItemStack remainingItem = itemStack.copy();

            // Directly damage the item
            remainingItem.setDamageValue(remainingItem.getDamageValue() + 1);

            // Check if the item is broken (durability is depleted)
            if (remainingItem.getDamageValue() >= remainingItem.getMaxDamage()) {
                return ItemStack.EMPTY; // Item is broken, return an empty stack
            }

            return remainingItem; // Return the damaged item

    }

    }
