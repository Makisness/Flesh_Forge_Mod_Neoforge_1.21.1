package net.makisness.fleshforgemod.item.custom;

import net.makisness.fleshforgemod.component.ModDataComponentTypes;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.Mod;

import static net.makisness.fleshforgemod.component.ModDataComponentTypes.DECAY_COMPONENT;

public class Flesh extends Item {
    public Flesh(Properties properties) {
        super(properties);
    }




    //Flesh Decays into Rotten Flesh
    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide) {
            if (level.getGameTime() % 200 != 0) {
                return; // Skip unless it's every 200th tick
            }
                System.out.println("MOD 200");
                Long currentTime = level.getGameTime();
                if (stack.get(DECAY_COMPONENT) == null) {
                    stack.set(DECAY_COMPONENT, currentTime);
                }
                Long decayDuration = 6000L;
                Long decayStartTime = stack.get(DECAY_COMPONENT);
                if (currentTime - decayStartTime >= decayDuration) {
                    // Replace with rotten flesh
                    ItemStack rottenFlesh = new ItemStack(Items.ROTTEN_FLESH, stack.getCount());
                    entity.getSlot(slotId).set(rottenFlesh);
                }


        }

    }

}


