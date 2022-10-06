package com.mrcrayfish.device.api.app.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class Label extends Component {

	protected String text;
	protected int width;
	protected boolean shadow = true;
	protected float scale = 1;
	protected int alignment = ALIGN_LEFT;

	protected int textColor = Color.WHITE.getRGB();
	
	/**
	 * Default label constructor
	 * 
	 * @param text the text to display
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 */
	public Label(String text, int left, int top) 
	{
		super(left, top);
		this.text = text;
	}

	@Override
	public void render(PoseStack poseStack, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
	{
		if (this.visible)
        {
			poseStack.pushPose();
			{
				poseStack.translate(xPosition, yPosition, 0);
				poseStack.scale(scale, scale, scale);
				if(alignment == ALIGN_RIGHT)
					poseStack.translate((int) -(mc.font.width(text) * scale), 0, 0);
				if(alignment == ALIGN_CENTER)
					poseStack.translate((int) -(mc.font.width(text) * scale) / (int) (2 * scale), 0, 0);
				Laptop.fontRenderer.draw(poseStack, text, 0, 0, textColor);
			}
			poseStack.popPose();
        }
	}
	
	/**
	 * Sets the text in the label
	 * 
	 * @param text the text
	 */
	public void setText(String text) 
	{
		this.text = text;
	}
	
	/**
	 * Sets the text color for this component
	 * 
	 * @param color the text color
	 */
	public void setTextColor(Color color)
	{
		this.textColor = color.getRGB();
	}
	
	/**
	 * Sets the whether shadow should show under the text
	 * 
	 * @param shadow if should render shadow
	 */
	public void setShadow(boolean shadow)
	{
		this.shadow = shadow;
	}
	
	/**
	 * Scales the text, essentially setting the font size. Minecraft
	 * does not support proper font resizing. The default scale is 1
	 * 
	 * @param scale the text scale
	 */
	public void setScale(double scale)
	{
		this.scale = (float) scale;
	}
	
	/**
	 * Sets the alignment of the text. Use {@link Component#ALIGN_LEFT} or
	 * {@link Component#ALIGN_RIGHT} to set alignment.
	 * 
	 * @param alignment the alignment type
	 */
	public void setAlignment(int alignment)
	{
		this.alignment = alignment;
	}
}
