package com.tarinoita.solsweetpotato.client.gui.elements;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.GuiUtils;

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
		
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1, 1, 1, alpha);
		RenderSystem.setShaderTexture(0, image.textureLocation);
		GuiUtils.drawTexturedModalRect(
			matrices,
			frame.x + (int) Math.floor((frame.width - imageWidth) / 2d),
			frame.y + (int) Math.floor((frame.height - imageHeight) / 2d),
			image.partOfTexture.x, image.partOfTexture.y,
			imageWidth, imageHeight,
			0
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
