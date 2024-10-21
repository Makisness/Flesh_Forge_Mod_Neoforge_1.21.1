package net.makisness.fleshforgemod.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.makisness.fleshforgemod.block.entity.custom.FleshGeneratorBlockEntity;
import net.makisness.fleshforgemod.fleshforgemod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FleshForgeScreen extends AbstractContainerScreen<FleshForgeMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID, "textures/gui/flesh_forge/flesh_forge_gui.png");
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(fleshforgemod.MODID, "textures/gui/flesh_forge/arrow_progress.png");


    private static final int ENERGY_LEFT = 155;
    private static final int ENERGY_WIDTH = 10;
    private static final int ENERGY_TOP = 9;
    private static final int ENERGY_HEIGHT = 63;


    public FleshForgeScreen(FleshForgeMenu menu, Inventory playerInventory, Component title) {
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
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        renderProgressArrow(guiGraphics, x, y);

        int power = menu.getPower();
        int p = (int) ((power / (float) FleshGeneratorBlockEntity.CAPACITY) * ENERGY_HEIGHT);

// Render the filled part from the bottom up
        guiGraphics.fillGradient(leftPos + ENERGY_LEFT, topPos + ENERGY_TOP + (ENERGY_HEIGHT - p), leftPos + ENERGY_LEFT + ENERGY_WIDTH, topPos + ENERGY_TOP + ENERGY_HEIGHT, 0xffff0000, 0xff000000);

// Render the empty part above the filled part
        guiGraphics.fill(leftPos + ENERGY_LEFT, topPos + ENERGY_TOP, leftPos + ENERGY_LEFT + ENERGY_WIDTH, topPos + ENERGY_TOP + (ENERGY_HEIGHT - p), 0xff330000);

    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if (menu.isCrafting()) {
            guiGraphics.blit(ARROW_TEXTURE, x + 83, y + 35, 0, 0, menu.getScaledArrowProgress(), 16, 24, 16);
        }
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);

        if (mouseX >= leftPos + ENERGY_LEFT && mouseX < leftPos + ENERGY_LEFT + ENERGY_WIDTH && mouseY >= topPos + ENERGY_TOP && mouseY < topPos + ENERGY_TOP + ENERGY_HEIGHT) {
            int power = menu.getPower();
            guiGraphics.renderTooltip(this.font, Component.literal(power + "RF"), mouseX, mouseY);
        }
    }
}
