package net.makisness.fleshforgemod.screen.custom;

import net.makisness.fleshforgemod.block.entity.custom.FleshGeneratorBlockEntity;
import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FleshGeneratorScreen extends AbstractContainerScreen<FleshGeneratorMenu> {

    private static final int ENERGY_LEFT = 96;
    private static final int ENERGY_WIDTH = 72;
    private static final int ENERGY_TOP = 8;
    private static final int ENERGY_HEIGHT = 8;

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID,"textures/gui/flesh_generator/generator.png");

    public FleshGeneratorScreen(FleshGeneratorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.inventoryLabelY = this.imageHeight - 110;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(GUI_TEXTURE,leftPos,topPos,0,0,this.imageWidth,this.imageHeight);
        int power = menu.getPower();
        int p = (int) ((power / (float) FleshGeneratorBlockEntity.CAPACITY) * ENERGY_WIDTH);
        guiGraphics.fillGradient(leftPos + ENERGY_LEFT, topPos + ENERGY_TOP,leftPos+ENERGY_LEFT+p,topPos + ENERGY_TOP + ENERGY_HEIGHT, 0xffff0000,0xff000000);
        guiGraphics.fill(leftPos + ENERGY_LEFT + p, topPos + ENERGY_TOP, leftPos + ENERGY_LEFT + ENERGY_WIDTH, topPos + ENERGY_TOP + ENERGY_HEIGHT, 0xff330000);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        //render tooltip with power if in the energybox
        if(mouseX >= leftPos + ENERGY_LEFT && mouseX < leftPos + ENERGY_LEFT + ENERGY_WIDTH && mouseY >= topPos + ENERGY_TOP && mouseY < topPos + ENERGY_TOP + ENERGY_HEIGHT){
            int power = menu.getPower();
            guiGraphics.renderTooltip(this.font, Component.literal(power + "RF"), mouseX,mouseY);
        }
    }
}
