package net.makisness.fleshforgemod.compat.Jade_WAILA;


import net.makisness.fleshforgemod.block.custom.FleshForgeBlock;
import net.makisness.fleshforgemod.block.custom.FleshGeneratorBlock;

import net.makisness.fleshforgemod.block.entity.custom.FleshForgeBlockEntity;
import net.makisness.fleshforgemod.block.entity.custom.FleshGeneratorBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class JadeFleshForgePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
          registration.registerBlockDataProvider(JadeHandlerGenerator.INSTANCE,  FleshGeneratorBlockEntity.class);
          registration.registerBlockDataProvider(JadeHandlerFleshForge.INSTANCE, FleshForgeBlockEntity.class);

    }
    @Override
    public void registerClient(IWailaClientRegistration registration) {
          registration.registerBlockComponent(JadeHandlerGenerator.INSTANCE, FleshGeneratorBlock.class);
          registration.registerBlockComponent(JadeHandlerFleshForge.INSTANCE, FleshForgeBlock.class);

    }
}
