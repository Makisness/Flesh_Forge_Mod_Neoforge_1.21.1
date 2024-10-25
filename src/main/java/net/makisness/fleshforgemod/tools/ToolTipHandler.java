package net.makisness.fleshforgemod.tools;

import net.makisness.fleshforgemod.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public class ToolTipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        Item item = itemStack.getItem();

        if (item == ModItems.FLESH_MASS.get()) {
            event.getToolTip().add(Component.literal("A lump organs and tissues.").withStyle(ChatFormatting.GRAY));
        }
    }
}
