/**
 * Much of the following code was adapted from Cyclic's storage bag code.
 * Copyright for portions of the code are held by Samson Basset (Lothrazar)
 * as part of Cyclic, under the MIT license.
 */
package com.kevun1.solpotato.item.foodcontainer;

import com.kevun1.solpotato.SOLPotato;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.items.CapabilityItemHandler;

public class FoodContainerScreen extends ContainerScreen<FoodContainer> {
    public FoodContainerScreen(FoodContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int x, int y) {
        this.drawBackground(matrices, new ResourceLocation(SOLPotato.MOD_ID, "textures/gui/inventory.png"));
        this.container.containerItem.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
          for (int i = 0; i < h.getSlots(); i++) {
            int row = (int) i / 9;
            int col = i % 9;
            int xPos = 7 + col * 18;
            int yPos = 7 + row * 18;

            this.drawSlot(matrices, xPos, yPos);
          }
        });
    }

    protected void drawBackground(MatrixStack ms, ResourceLocation gui) {
        this.minecraft.getTextureManager().bindTexture(gui);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(ms, relX, relY, 0, 0, this.xSize, this.ySize);
    }

    protected void drawSlot(MatrixStack ms, int x, int y, ResourceLocation texture, int size) {
        this.minecraft.getTextureManager().bindTexture(texture);
        blit(ms, guiLeft + x, guiTop + y, 0, 0, size, size, size, size);
    }

    protected void drawSlot(MatrixStack ms, int x, int y) {
        drawSlot(ms, x, y, new ResourceLocation(SOLPotato.MOD_ID, "textures/gui/slot.png"), 18);
    }
}
