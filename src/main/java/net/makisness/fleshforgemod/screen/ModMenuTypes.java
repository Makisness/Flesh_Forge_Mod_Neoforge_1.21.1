package net.makisness.fleshforgemod.screen;

import net.makisness.fleshforgemod.fleshforgemod;
import net.makisness.fleshforgemod.screen.custom.FleshForgeMenu;
import net.makisness.fleshforgemod.screen.custom.FleshGeneratorMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, fleshforgemod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<FleshForgeMenu>> FLESH_FORGE_MENU =
            registerMenuType("fleshforge_menu", FleshForgeMenu::new);
    public static final Supplier<MenuType<FleshGeneratorMenu>> FLESH_GENERATOR_MENU = MENUS.register("flesh_generator_block",
            () -> IMenuTypeExtension.create((containerId, inv, data) -> new FleshGeneratorMenu(containerId, inv.player, data.readBlockPos())));

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name,
                                                                                                              IContainerFactory<T> factory){
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void register(IEventBus eventBus){
        MENUS.register(eventBus);
    }
}
