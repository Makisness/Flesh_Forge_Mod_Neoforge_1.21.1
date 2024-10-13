package net.makisness.fleshforgemod;

import net.makisness.fleshforgemod.block.ModBlocks;
import net.makisness.fleshforgemod.block.entity.ModBlockEntities;
import net.makisness.fleshforgemod.component.ModDataComponentTypes;
import net.makisness.fleshforgemod.item.ModCreativeTabs;
import net.makisness.fleshforgemod.item.ModItems;
import net.makisness.fleshforgemod.recipe.ModRecipes;
import net.makisness.fleshforgemod.screen.ModMenuTypes;
import net.makisness.fleshforgemod.screen.custom.FleshForgeScreen;
import net.makisness.fleshforgemod.screen.custom.FleshGeneratorScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.Random;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(fleshforgemod.MODID)
public class fleshforgemod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "fleshforgemod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();



    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public fleshforgemod(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModDataComponentTypes.register(modEventBus);
        ModRecipes.register(modEventBus);



        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event){
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.FLESH_GENERATOR_BE.get(), (o, direction) -> o.getItemHandler());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, ModBlockEntities.FLESH_GENERATOR_BE.get(), (o, direction) -> o.getEnergyHandler());
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event){
            event.register(ModMenuTypes.FLESH_FORGE_MENU.get(), FleshForgeScreen::new);
            event.register(ModMenuTypes.FLESH_GENERATOR_MENU.get(), FleshGeneratorScreen::new);
        }

    }

    @EventBusSubscriber(modid = MODID)
    public class MobDropHandler{
        private static final Random RANDOM = new Random();
        @SubscribeEvent
        public static void onMobDrops(LivingDropsEvent event){
            LivingEntity entity = event.getEntity();

            double hostiledropChance = 0.7;
            double passivedropChance = 0.3;

            double dropChance = entity.getType().getCategory() == MobCategory.MONSTER ? hostiledropChance : passivedropChance;

            if(RANDOM.nextDouble()<dropChance) {
                ItemStack customItemStack = new ItemStack(ModItems.FLESH_MASS.get());

                event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), customItemStack));
            }
        }
    }


}
