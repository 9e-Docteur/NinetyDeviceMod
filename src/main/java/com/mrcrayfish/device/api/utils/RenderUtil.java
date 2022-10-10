package com.mrcrayfish.device.api.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.AppInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class RenderUtil
{
	public static void renderItem(int x, int y, ItemStack stack, boolean overlay)
	{
		//RenderSystem.disableDepth();
		//RenderSystem.enableLighting();
		//RenderHelper.enableGUIStandardItemLighting();
		Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(stack, x, y);
		if(overlay) Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(Minecraft.getInstance().font, stack, x, y);
		//RenderSystem.enableAlpha();
		//RenderSystem.disableLighting();
	}
	
	public static void fillWithTexture(double x, double y, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
		fillWithTexture(x, y, 0, u, v, width, height, textureWidth, textureHeight);
    }
	
	/**
	 * Texture size must be 256x256
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param u
	 * @param v
	 * @param width
	 * @param height
	 * @param textureWidth
	 * @param textureHeight
	 */
	public static void fillWithTexture(double x, double y, double z, float u, float v, int width, int height, float textureWidth, float textureHeight)
    {
		float scale = 0.00390625f;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		BufferBuilder buffer = Tesselator.getInstance().getBuilder();
		try {
			buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		} catch (IllegalStateException e) {
			buffer.end();
			buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		}
		buffer.vertex(x, y + height, z).uv(u * scale, (v + textureHeight) * scale).endVertex();
		buffer.vertex(x + width, y + height, z).uv((u + textureWidth) * scale, (v + textureHeight) * scale).endVertex();
		buffer.vertex(x + width, y, z).uv((u + textureWidth) * scale, v * scale).endVertex();
		buffer.vertex(x, y, z).uv(u * scale, v * scale).endVertex();
		BufferUploader.drawWithShader(buffer.end());
    }

	public static void fillWithFullTexture(double x, double y, float u, float v, int width, int height)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		BufferBuilder buffer = Tesselator.getInstance().getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buffer.vertex(x, y + height, 0).uv(0, 1).endVertex();
		buffer.vertex(x + width, y + height, 0).uv(1, 1).endVertex();
		buffer.vertex(x + width, y, 0).uv(1, 0).endVertex();
		buffer.vertex(x, y, 0).uv(0, 0).endVertex();
		BufferUploader.drawWithShader(buffer.end());
	}

	public static void fillWithTexture(double x, double y, float u, float v, int width, int height, float textureWidth, float textureHeight, int sourceWidth, int sourceHeight)
	{
		float scaleWidth = 1f / sourceWidth;
		float scaleHeight = 1f / sourceHeight;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		BufferBuilder buffer = Tesselator.getInstance().getBuilder();
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		buffer.vertex(x, y + height, 0).uv(u * scaleWidth, (v + textureHeight) * scaleHeight).endVertex();
		buffer.vertex(x + width, y + height, 0).uv((u + textureWidth) * scaleWidth, (v + textureHeight) * scaleHeight).endVertex();
		buffer.vertex(x + width, y, 0).uv((u + textureWidth) * scaleWidth, v * scaleHeight).endVertex();
		buffer.vertex(x, y, 0).uv(u * scaleWidth, v * scaleHeight).endVertex();
		BufferUploader.drawWithShader(buffer.end());
	}

	public static void drawApplicationIcon(@Nullable AppInfo info, double x, double y)
	{
		//TODO: Reset color RenderSystem.color(1.0F, 1.0F, 1.0F);
		Minecraft.getInstance().getTextureManager().bindForSetup(Laptop.ICON_TEXTURES);
		if(info != null)
		{
			fillWithTexture(x, y, info.getIconU(), info.getIconV(), 14, 14, 14, 14, 224, 224);
		}
		else
		{
			fillWithTexture(x, y, 0, 0, 14, 14, 14, 14, 224, 224);
		}
	}

	public static void drawStringClipped(PoseStack poseStack, String text, int x, int y, int width, int color, boolean shadow)
	{
		Minecraft.getInstance().font.draw(poseStack, clipStringToWidth(text, width) + ChatFormatting.RESET, x, y, color);
	}

	public static String clipStringToWidth(String text, int width)
	{
		Font fontRenderer = Laptop.fontRenderer;
		String clipped = text;
		if(fontRenderer.width(clipped) > width)
		{
			clipped = fontRenderer.plainSubstrByWidth(clipped, width - 8) + "...";
		}
		return clipped;
	}

	public static boolean isMouseInside(int mouseX, int mouseY, int x1, int y1, int x2, int y2)
	{
		return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
	}

	public static int color(int color, int defaultColor)
	{
		return color != -1 && color > 0 ? color : defaultColor;
	}
}
