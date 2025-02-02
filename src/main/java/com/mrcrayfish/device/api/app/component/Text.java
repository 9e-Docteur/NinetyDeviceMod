package com.mrcrayfish.device.api.app.component;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Text extends Component 
{
	protected String rawText;
	protected List<String> lines;
	protected int width;
	protected int padding;
	protected boolean shadow = false;
	
	protected int textColor = Color.WHITE.getRGB();

	private WordListener wordListener = null;
	
	/**
	 * Default text constructor
	 * 
	 * @param text the text to display
	 * @param left how many pixels from the left
	 * @param top how many pixels from the top
	 * @param width the max width
	 */
	public Text(String text, int left, int top, int width)
	{
		super(left, top);
		this.width = width;
		this.setText(text);
	}

	@Override
	public void render(PoseStack poseStack, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
	{
		if (this.visible)
        {
			for(int i = 0; i < lines.size(); i++)
			{
				String text = lines.get(i);
				while (text != null && text.endsWith("\n"))
				{
					text = text.substring(0, text.length() - 1);
				}
				Laptop.fontRenderer.draw(poseStack, text, x + padding, y + (i * 10) + padding, textColor);
			}
        }
	}
	
	/**
	 * Sets the text for this component 
	 * 
	 * @param text the text
	 */
	public void setText(String text)
	{
		rawText = text;
		text = text.replace("\\n", "\n");
		System.out.println(Laptop.getFontRenderer().plainSubstrByWidth(text, width - padding * 2));
		var a = new ArrayList<String>();
		Laptop.getFontRenderer().getSplitter().splitLines(FormattedText.of(text), width - padding * 2, Style.EMPTY).forEach(b -> a.add(b.getString()));
		this.lines = a;
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
	 * @param shadow the text color
	 */
	public void setShadow(boolean shadow)
	{
		this.shadow = shadow;
	}

	/**
	 *
	 * @param padding
	 */
	public void setPadding(int padding)
	{
		this.padding = padding;
		this.updateLines();
	}

	private void updateLines()
	{
		this.setText(rawText);
	}

	@Override
	protected void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		if(GuiHelper.isMouseWithin(mouseX, mouseY, xPosition + padding, yPosition + padding, width - padding * 2, getHeight() - padding * 2))
		{
			if(this.wordListener != null && lines.size() > 0)
			{
				int lineIndex = (mouseY - (yPosition + padding)) / 10;
				if(lineIndex < lines.size())
				{
					int cursorX = mouseX - (xPosition + padding);
					String line = lines.get(lineIndex);
					int index = Laptop.fontRenderer.plainSubstrByWidth(line, cursorX).length();
					String clickedWord = getWord(line, index);
					if(clickedWord != null)
					{
						this.wordListener.onWordClicked(clickedWord, mouseButton);
					}
				}
			}
		}
	}

	private String getWord(String line, int index)
	{
		if(index >= line.length() || line.charAt(index) == ' ')
			return null;

		int startIndex = index;
		while(startIndex > 0 && line.charAt(startIndex - 1) != ' ') --startIndex;

		int endIndex = index;
		while(endIndex + 1 < line.length() && line.charAt(endIndex + 1) != ' ') ++endIndex;

		endIndex = Math.min(endIndex + 1, line.length());

		return ChatFormatting.stripFormatting(line.substring(startIndex, endIndex));
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return lines.size() * Minecraft.getInstance().font.lineHeight + lines.size() - 1 + padding * 2;
	}

	public void setWordListener(WordListener wordListener)
	{
		this.wordListener = wordListener;
	}

	public interface WordListener
	{
		void onWordClicked(String word, int mouseButton);
	}
}
