package net.makisness.fleshforgemod.item;

import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(fleshforgemod.MODID);

    public static final DeferredItem<Item> BIO_CPU = ITEMS.register("bio_cpu",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> FLESH_MASS = ITEMS.register("flesh_mass",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RAW_BAUXITE = ITEMS.register("raw_bauxite",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BUCKET_FLESH = ITEMS.register("bucket_flesh",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BUCKET_GELATIN = ITEMS.register("bucket_gelatin",
            () -> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
