package net.makisness.fleshforgemod.item;

import net.makisness.fleshforgemod.block.ModBlocks;
import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> Creative_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, fleshforgemod.MODID);

    public static final Supplier<CreativeModeTab> Flesh_Forge_Ingredients_Tab = Creative_MODE_TAB.register("flesh_forge_ingredients_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.BIO_CPU.get()))
                    .title(Component.translatable("creativetab.fleshforgemod.flesh_forge_ingredients_tab"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModItems.BIO_CPU);
                        output.accept(ModItems.FLESH_MASS);
                        output.accept(ModItems.RAW_BAUXITE);
                        output.accept(ModItems.BUCKET_FLESH);
                        output.accept(ModItems.BUCKET_GELATIN);
                        output.accept(ModItems.ROUND_KNIFE);

                    }).build());

    public static final Supplier<CreativeModeTab> Flesh_Forge_Blocks_Tab = Creative_MODE_TAB.register("flesh_forge_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.FLESH_FORGE.get()))
                    .title(Component.translatable("creativetab.fleshforgemod.flesh_forge_blocks_tab"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModBlocks.BAUXITE_ORE);
                        output.accept(ModBlocks.DEEPSLATE_BAUXITE_ORE);
                        output.accept(ModBlocks.FLESH_GENERATOR);
                        output.accept(ModBlocks.FLESH_FORGE);

                    }).build());

    public static void register(IEventBus eventBus){
        Creative_MODE_TAB.register(eventBus);
    }
}
