package com.tarinoita.solapplepie.client.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class UIImage extends UIElement {
	public Image image;
	public float alpha = 1;
	
	public UIImage(Image image) {
		this(new Rectangle(image.partOfTexture.getSize()), image);
	}
	
	public UIImage(Rectangle frame, Image image) {
		super(frame);
		
		this.image = image;
	}
	
	@Override
	protected void render(PoseStack matrices) {
		super.render(matrices);
		
		int imageWidth = image.partOfTexture.width;
		int imageHeight = image.partOfTexture.height;
		
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, image.textureLocation);

		GuiComponent.blit(
			matrices,
			frame.x + (int) Math.floor((frame.width - imageWidth) / 2d),
			frame.y + (int) Math.floor((frame.height - imageHeight) / 2d),
			0,
			image.partOfTexture.x, image.partOfTexture.y,
			imageWidth, imageHeight,
			256, 256
		);
	}
	
	public static class Image {
		public final ResourceLocation textureLocation;
		public final Rectangle partOfTexture;
		
		public Image(ResourceLocation textureLocation, Rectangle partOfTexture) {
			this.textureLocation = textureLocation;
			this.partOfTexture = partOfTexture;
		}
	}
}
