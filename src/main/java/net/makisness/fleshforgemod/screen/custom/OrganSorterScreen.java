package net.makisness.fleshforgemod.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class OrganSorterScreen extends AbstractContainerScreen<OrganSorterMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID,"textures/gui/organ_sorter/organ_sorter_gui.png");
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID,"textures/gui/flesh_forge/arrow_progress.png");




    public OrganSorterScreen(OrganSorterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }


    @Override
    protected void init() {
        super.init();

        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
        RenderSystem.setShaderTexture(0,GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y,0,0, imageWidth, imageHeight);
        renderProgressArrow(guiGraphics, x, y);

    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y){
        if(menu.isCrafting()){
            guiGraphics.blit(ARROW_TEXTURE,x + 74,y + 35,0,0, menu.getScaledArrowProgress(),16,24,16);
        }
    }
}
