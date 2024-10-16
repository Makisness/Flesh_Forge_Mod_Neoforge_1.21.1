package net.makisness.fleshforgemod.compat.Jade_WAILA;

import net.makisness.fleshforgemod.block.entity.custom.FleshForgeBlockEntity;
import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.Util;
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
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;

public class JadeHandlerFleshForge implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final JadeHandlerFleshForge INSTANCE = new JadeHandlerFleshForge();

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID, "flesh_forge");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        //I WROTE ALL OF THIS BEFORE PROPERLY REGISTERING MY BLOCKS ONLY TO FIND OUT THIS CODE WAS REDUNDANT BECAUSE MOST IF IT IS HANDLED BY DEFAULT...
        //IM TOO ATTACHED NOW TO THROW IT AWAY. IT WILL REST HERE FOR NOW IN THE COMMENT GRAVEYARD.

//        int energy = blockAccessor.getServerData().getInt("energy");
//        int maxEnergy = blockAccessor.getServerData().getInt("max_energy");
//
//        CompoundTag data = blockAccessor.getServerData();
//        if (data.contains("progress")) {
//            int progress = data.getInt("progress");
//            ListTag forgeItems = data.getList("forge", 10);
//            NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
//
//            for (int i = 0; i < forgeItems.size(); ++i) {
//                inventory.set(i, ItemStack.parseOptional(blockAccessor.getLevel().registryAccess(), forgeItems.getCompound(i)));
//            }
//
//            IElementHelper helper = IElementHelper.get();
//            int total = data.getInt("maxProgress");
//            iTooltip.add(helper.item((ItemStack) inventory.get(0)));
//            iTooltip.append(helper.item((ItemStack) inventory.get(1)));
//            iTooltip.append(helper.spacer(4, 0));
//            iTooltip.append(helper.progress((float) progress / (float) total).translate(new Vec2(-2.0F, 0.0F)));
//            iTooltip.append(helper.item((ItemStack) inventory.get(2)));
//        }
////        if(data.contains("energy")){
////            IElementHelper helper = IElementHelper.get();
////            iTooltip.add(
////                    helper.progress(getProgress(energy, maxEnergy),
////                            Component.translatable("tooltip.waila.flesh_forge.energy", energy, maxEnergy),
////                            helper.progressStyle().color(0xAA0000, 0xAA0000).textColor(-1),
////                            Util.make(BoxStyle.GradientBorder.DEFAULT_VIEW_GROUP,
////                                    style -> style.borderColor = new int[] { 0xFF555555, 0xFF555555, 0xFF555555, 0xFF555555 }),
////                            true));
////        }
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor blockAccessor) {
//        FleshForgeBlockEntity forgeBE = (FleshForgeBlockEntity) blockAccessor.getBlockEntity();
//
//        BlockEntity var4 = blockAccessor.getBlockEntity();
//        if (var4 instanceof FleshForgeBlockEntity forge) {
//            if (!forge.isInventoryEmpty()) {
//                ListTag items = new ListTag();
//
//                for (int i = 0; i < 4; ++i) {
//                    items.add(forge.getItem(i).saveOptional(blockAccessor.getLevel().registryAccess()));
//                    tag.put("forge", items);
//                }
//                tag.putInt("progress", forgeBE.getProgress());
//                tag.putInt("maxProgress", forgeBE.getMaxProgress());
//            }
//            tag.putInt("energy", forgeBE.getStoredPower());
//            tag.putInt("max_energy", forgeBE.getMaxStoredPower());
//        }
    }
//    private float getProgress(int energy, int maxEnergy) {
//        return (float) energy /maxEnergy;
//    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
