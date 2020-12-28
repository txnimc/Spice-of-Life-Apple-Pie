package com.kevun1.solpotato.client.gui;

import com.kevun1.solpotato.SOLPotato;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
final class PageFlipButton extends Button {
	private static final ResourceLocation texture = SOLPotato.resourceLocation("textures/gui/food_book.png");
	public static final int width = 23;
	public static final int height = 13;
	
	private final Direction direction;
	private final Pageable pageable;
	
	PageFlipButton(int x, int y, Direction direction, Pageable pageable) {
		super(x, y, 23, 13, new StringTextComponent(""), (button) -> ((PageFlipButton) button).changePage());
		
		this.direction = direction;
		this.pageable = pageable;
	}
	
	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
		if (!visible) return;
		
		int textureX = 0;
		
		boolean isHovered = x <= mouseX && mouseX < x + width && y <= mouseY && mouseY < y + height;
		if (isHovered) {
			textureX += 23;
		}
		
		int textureY = direction == Direction.FORWARD ? 192 : 205;
		
		Minecraft.getInstance().getTextureManager().bindTexture(texture);
		blit(matrices, x, y, textureX, textureY, 23, 13);
	}
	
	public void updateState() {
		visible = pageable.isWithinRange(pageable.getCurrentPageNumber() + direction.distance);
	}
	
	private void changePage() {
		pageable.switchToPage(pageable.getCurrentPageNumber() + direction.distance);
	}
	
	enum Direction {
		FORWARD(1),
		BACKWARD(-1);
		
		final int distance;
		
		Direction(int distance) {
			this.distance = distance;
		}
	}
	
	interface Pageable {
		void switchToPage(int pageNumber);
		
		int getCurrentPageNumber();
		
		boolean isWithinRange(int pageNumber);
	}
}
