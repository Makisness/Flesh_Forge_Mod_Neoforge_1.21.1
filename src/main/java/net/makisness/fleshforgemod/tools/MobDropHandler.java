package net.makisness.fleshforgemod.tools;

import net.makisness.fleshforgemod.item.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.Random;
import java.util.Set;

public class MobDropHandler {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onMobDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();

        // Drop flesh mass with health multiplier
        addConditionalDrop(event, entity, ModItems.FLESH_MASS.get(), 0.5f, 0.2f, Set.of(EntityType.SKELETON, EntityType.STRAY));

        // Drop fresh flesh with health multiplier
        addConditionalDrop(event, entity, ModItems.FRESH_FLESH.get(), 0.3f, 0.1f, Set.of(EntityType.SKELETON, EntityType.STRAY));
    }

    /**
     * Adds a drop to the mob's drops based on the given conditions.
     * @param event The mob drops event.
     * @param entity The entity that drops the item.
     * @param item The item to drop.
     * @param hostileDropChance The base drop chance for hostile mobs.
     * @param passiveDropChance The base drop chance for passive mobs.
     * @param excludedMobs A set of entity types to exclude from dropping the item.
     */

    private static void addConditionalDrop(LivingDropsEvent event, LivingEntity entity, Item item, float hostileDropChance, float passiveDropChance, Set<EntityType<?>> excludedMobs) {
        // Exclude specified mob types
        if (excludedMobs.contains(entity.getType())) {
            return;
        }

        // Determine base drop chance
        float dropChance = entity.getType().getCategory() == MobCategory.MONSTER ? hostileDropChance : passiveDropChance;

        // Apply a health multiplier: increase drop chance by 5% for every 5 health points above 10
        float healthMultiplier = 1.0f + ((entity.getMaxHealth() - 10) / 5) * 0.05f;
        dropChance *= healthMultiplier;

        // Attempt to drop the item based on the final drop chance
        if (RANDOM.nextFloat() < dropChance) {
            ItemStack itemStack = new ItemStack(item);
            event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), itemStack));
        }
    }
}
