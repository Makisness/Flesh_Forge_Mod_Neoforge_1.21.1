package net.makisness.fleshforgemod.compat.Jade_WAILA;

import net.makisness.fleshforgemod.block.entity.custom.FleshGeneratorBlockEntity;
import net.makisness.fleshforgemod.block.entity.custom.TestBlockEntity;
import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;

public class JadeHandlerTestBlock implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final JadeHandlerTestBlock INSTANCE = new JadeHandlerTestBlock();

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID, "testblock");


    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        int energy = blockAccessor.getServerData().getInt("energy");
        int maxEnergy = blockAccessor.getServerData().getInt("max_energy");
        IElementHelper helper = IElementHelper.get();
        CompoundTag data = blockAccessor.getServerData();
            iTooltip.add(
                    helper.progress(getProgress(energy, maxEnergy),
                            Component.translatable("tooltip.waila.test.energy", energy, maxEnergy),
                            helper.progressStyle().color(0xAA0000, 0xAA0000).textColor(-1),
                            Util.make(BoxStyle.GradientBorder.DEFAULT_VIEW_GROUP,
                                    style -> style.borderColor = new int[] { 0xFF555555, 0xFF555555, 0xFF555555, 0xFF555555 }),
                            true));
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor blockAccessor) {
        TestBlockEntity testBlockEntity = (TestBlockEntity) blockAccessor.getBlockEntity();

        BlockEntity var4 = blockAccessor.getBlockEntity();
        if (var4 instanceof TestBlockEntity test) {
            if(test.getMaxStoredPower()!=0){
                tag.putInt("energy", testBlockEntity.getStoredPower());
                tag.putInt("max_energy", testBlockEntity.getMaxStoredPower());
            }
        }
    }

    private float getProgress(int energy, int maxEnergy) {
        return (float) energy /maxEnergy;
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
