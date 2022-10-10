package com.mrcrayfish.device.api.app;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.api.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;

public interface IIcon
{
	ResourceLocation getIconAsset();
	
	int getIconSize();

	int getGridWidth();

	int getGridHeight();

	/**
	 * Width of the source texture in pixels.
	 * @return The source width.
	 */
	int getSourceWidth();

	/**
	 * Height of the source texture in pixels.
	 * @return The source height.
	 */
	int getSourceHeight();
	
	int getU();

    int getV();

    int getOrdinal();

    default void draw(PoseStack poseStack, Minecraft mc, int x, int y)
	{
		RenderSystem.setShaderColor(1F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, getIconAsset());
		int size = getIconSize();
		int assetWidth = getGridWidth() * size;
		int assetHeight = getGridHeight() * size;
		GuiComponent.blit(poseStack, x, y, getU(), getV(), size, size, size, size, assetWidth, assetHeight);
	}
}
