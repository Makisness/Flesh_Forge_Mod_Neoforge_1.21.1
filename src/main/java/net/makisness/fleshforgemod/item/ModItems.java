package net.makisness.fleshforgemod.item;

import net.makisness.fleshforgemod.fleshforgemod;
import net.makisness.fleshforgemod.item.custom.roundKnife;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(fleshforgemod.MODID);


    //Ingots & Ores
    public static final DeferredItem<Item> ALUMINUM_INGOT = ITEMS.register("aluminum_ingot",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> RAW_BAUXITE = ITEMS.register("raw_bauxite",
            () -> new Item(new Item.Properties()));

    //Tools
    public static final DeferredItem<Item> ROUND_KNIFE = ITEMS.register("round_knife",
            () -> new roundKnife(new Item.Properties().durability(96)));


    //Crafting Items
    public static final DeferredItem<Item> BIO_CPU = ITEMS.register("bio_cpu",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> FLESH_MASS = ITEMS.register("flesh_mass",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> MUSCLE_FIBERS = ITEMS.register("muscle_fibers",
            () -> new Item(new Item.Properties()));


    //Fluids
    public static final DeferredItem<Item> BUCKET_FLESH = ITEMS.register("bucket_flesh",
            () -> new BucketItem(Fluids.WATER, (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<Item> BUCKET_GELATIN = ITEMS.register("bucket_gelatin",
            () -> new BucketItem(Fluids.WATER, (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1)));







    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
