/**
 * Much of the following code was adapted from Cyclic's storage bag code.
 * Copyright for portions of the code are held by Samson Basset (Lothrazar)
 * as part of Cyclic, under the MIT license.
 */
package com.tarinoita.solsweetpotato.item.foodcontainer;

import com.tarinoita.solsweetpotato.SOLSweetPotato;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.CapabilityItemHandler;

public class FoodContainerScreen extends AbstractContainerScreen<FoodContainer> {
    public FoodContainerScreen(FoodContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        super.render(ms, mouseX, mouseY, partialTicks);
        this.renderTooltip(ms, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack matrices, float partialTicks, int x, int y) {
        this.drawBackground(matrices, new ResourceLocation(SOLSweetPotato.MOD_ID, "textures/gui/inventory.png"));
        this.menu.containerItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            int slotsPerRow = h.getSlots();
            if (h.getSlots() > 9) {
                slotsPerRow = h.getSlots() / 2;
            }
            int xStart = (2*8 + 9*18 - slotsPerRow * 18) / 2;
            int yStart = 17 + 18;
            if (h.getSlots() > 9) {
                yStart = 17 + (84-36-23)/2;
            }
            for (int i = 0; i < h.getSlots(); i++) {
                int row = i / slotsPerRow;
                int col = i % slotsPerRow;
                int xPos = xStart - 1 + col * 18;
                int yPos = yStart - 1 + row * 18;

                this.drawSlot(matrices, xPos, yPos);
          }
        });
    }

    protected void drawBackground(PoseStack ms, ResourceLocation gui) {
        this.minecraft.getTextureManager().bindForSetup(gui);
        int relX = (this.width - this.getXSize()) / 2;
        int relY = (this.height - this.getYSize()) / 2;
        this.blit(ms, relX, relY, 0, 0, this.getXSize(), this.getYSize());
    }

    protected void drawSlot(PoseStack ms, int x, int y, ResourceLocation texture, int size) {
        this.minecraft.getTextureManager().bindForSetup(texture);
        blit(ms, this.getGuiLeft() + x, this.getGuiTop() + y, 0, 0, size, size, size, size);
    }

    protected void drawSlot(PoseStack ms, int x, int y) {
        drawSlot(ms, x, y, new ResourceLocation(SOLSweetPotato.MOD_ID, "textures/gui/slot.png"), 18);
    }
}
