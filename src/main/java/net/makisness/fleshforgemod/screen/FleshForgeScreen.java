package net.makisness.fleshforgemod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FleshForgeScreen extends AbstractContainerScreen<FleshForgeMenu> {
    private static final ResourceLocation TEXUTURE =
             ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID, "textures/gui/gem_polishing_station_gui");


    public FleshForgeScreen(FleshForgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 100000;
        this.titleLabelY = 100000;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
        RenderSystem.setShaderTexture(0,TEXUTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) /2;

        guiGraphics.blit(TEXUTURE,x,y,0,0,imageWidth,imageHeight);

        renderProgressArrow(guiGraphics,x,y);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y){
        if(menu.isCrafting()){
            guiGraphics.blit(TEXUTURE,x+85,y+30,176,0,8,menu.getScaledProgress());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics,mouseX,mouseY);
    }
}
