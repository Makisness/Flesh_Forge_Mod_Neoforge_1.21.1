package net.makisness.fleshforgemod.compat.Jade_WAILA;

import net.makisness.fleshforgemod.block.entity.custom.FleshForgeBlockEntity;
import net.makisness.fleshforgemod.block.entity.custom.FleshGeneratorBlockEntity;
import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.*;

public class JadeHandlerGenerator implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final JadeHandlerGenerator INSTANCE = new JadeHandlerGenerator();

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID, "generator");

    private static final ResourceLocation LIT_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("container/furnace/lit_progress");
    private static final ResourceLocation BASE_LIT_SPRITE = ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID,"textures/gui/flesh_forge/unlit_burn_sprite");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        int energy = blockAccessor.getServerData().getInt("energy");
        int maxEnergy = blockAccessor.getServerData().getInt("max_energy");

        CompoundTag data = blockAccessor.getServerData();
        if (data.contains("energy")) {
            ListTag generatorItems = data.getList("generator", 10);
            NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

            for (int i = 0; i < generatorItems.size(); ++i) {
                inventory.set(i, ItemStack.parseOptional(blockAccessor.getLevel().registryAccess(), generatorItems.getCompound(i)));
            }

            IElementHelper helper = IElementHelper.get();
            int total = data.getInt("maxBurnTime");
            int progress = data.getInt("burnTime");


            iTooltip.append(helper.spacer(2, 0));

            IElement burnTimeProgress = helper.progress((float) progress*-1 / (float) total).translate(new Vec2(-2.0F, 0.0F));
            ProgressStyle progressStyle = helper.progressStyle().overlay(helper.sprite(LIT_PROGRESS_SPRITE,16,16));
            iTooltip.add(
                    helper.progress(getProgress(energy, maxEnergy),
                            Component.translatable("tooltip.waila.generator.energy", energy, maxEnergy),
                            helper.progressStyle().color(0xAA0000, 0xAA0000).textColor(-1),
                            Util.make(BoxStyle.GradientBorder.DEFAULT_VIEW_GROUP,
                                    style -> style.borderColor = new int[] { 0xFF555555, 0xFF555555, 0xFF555555, 0xFF555555 }),
                            true));

            iTooltip.append(helper.item((ItemStack) inventory.get(0)));
        }
    }

    private float getProgress(int energy, int maxEnergy) {
        return (float) energy /maxEnergy;
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor blockAccessor) {
        FleshGeneratorBlockEntity generatorBE = (FleshGeneratorBlockEntity) blockAccessor.getBlockEntity();

        BlockEntity var4 = blockAccessor.getBlockEntity();
        if (var4 instanceof FleshGeneratorBlockEntity generator) {
            if (!generator.isInventoryEmpty()) {
                ListTag items = new ListTag();

                items.add(generator.getItem(0).saveOptional(blockAccessor.getLevel().registryAccess()));
                tag.put("generator", items);
            }
            if(generator.getStoredPower()!=0){

                tag.putInt("burnTime", generator.getBurnTimePassed());
                tag.putInt("maxBurnTime", generator.getMaxBurnTime());
                tag.putInt("energy", generatorBE.getStoredPower());
                tag.putInt("max_energy", generatorBE.getMaxStoredPower());
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
