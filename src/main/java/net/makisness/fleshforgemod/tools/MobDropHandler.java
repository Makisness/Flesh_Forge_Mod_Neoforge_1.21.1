package net.makisness.fleshforgemod.tools;

import net.makisness.fleshforgemod.item.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.Random;

public class MobDropHandler {

    private static final Random RANDOM = new Random();


    @SubscribeEvent
    public static void onMobDrops(LivingDropsEvent event){
        LivingEntity entity = event.getEntity();

        float hostiledropChance = 0.7f;
        float passivedropChance = 0.3f;

        float dropChance = entity.getType().getCategory() == MobCategory.MONSTER ? hostiledropChance : passivedropChance;

        if(RANDOM.nextDouble()<dropChance) {
            ItemStack customItemStack = new ItemStack(ModItems.FLESH_MASS.get());

            event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), customItemStack));
        }
    }
}
