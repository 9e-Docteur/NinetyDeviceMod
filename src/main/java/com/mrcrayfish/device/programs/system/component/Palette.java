package com.mrcrayfish.device.programs.system.component;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.ComboBox;
import com.mrcrayfish.device.api.app.component.Slider;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GLHelper;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;

/**
 * Author: MrCrayfish
 */
public class Palette extends Component
{
    private final ComboBox.Custom<Integer> colorPicker;

    private Color currentColor = Color.RED;

    private Slider colorSlider;

    /**
     * The default constructor for a component.
     * <p>
     * Laying out components is simply relative positioning. So for left (x position),
     * specific how many pixels from the left of the application window you want
     * it to be positioned at. The top is the same, but instead from the top (y position).
     *
     * @param left how many pixels from the left
     * @param top  how many pixels from the top
     */
    public Palette(int left, int top, ComboBox.Custom<Integer> colorPicker)
    {
        super(left, top);
        this.colorPicker = colorPicker;
    }

    @Override
    protected void init(Layout layout)
    {
        colorSlider = new Slider(5, 58, 52);
        colorSlider.setSlideListener(percentage ->
        {
            if(percentage >= (1.0 / 6.0) * 5.0)
            {
                currentColor = new Color(1.0F, 1.0F - (percentage - (1.0F / 6.0F) * 5.0F) * 6.0F, 0.0F);
            }
            else if(percentage >= (1.0 / 6.0) * 4.0)
            {
                currentColor = new Color((percentage - ((1.0F / 6.0F) * 4.0F)) * 6.0F, 1.0F, 0.0F);
            }
            else if(percentage >= (1.0 / 6.0) * 3.0)
            {
                currentColor = new Color(0.0F, 1.0F, 1.0F - (percentage - ((1.0F / 6.0F) * 3.0F)) * 6.0F);
            }
            else if(percentage >= (1.0 / 6.0) * 2.0)
            {
                currentColor = new Color(0.0F, (percentage - ((1.0F / 6.0F) * 2.0F)) * 6.0F, 1.0F);
            }
            else if(percentage >= (1.0 / 6.0))
            {
                currentColor = new Color(1.0F - (percentage - ((1.0F / 6.0F))) * 6.0F, 0.0F, 1.0F);
            }
            else if(percentage >= (1.0 / 6.0) * 0.0)
            {
                currentColor = new Color(1.0F, 0.0F, percentage * 6.0F);
            }
        });
        layout.addComponent(colorSlider);
    }

    @Override
    protected void render(PoseStack poseStack, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        Gui.fill(poseStack, x, y, x + 52, y + 52, Color.DARK_GRAY.getRGB());

        //RenderSystem.disableLighting();
        //RenderSystem.disableTexture2D();
        RenderSystem.enableBlend();
        //RenderSystem.disableAlpha();
        //RenderSystem.tryBlendFuncSeparate(RenderSystem.SourceFactor.SRC_ALPHA, RenderSystem.DestFactor.ONE_MINUS_SRC_ALPHA, RenderSystem.SourceFactor.ONE, RenderSystem.DestFactor.ZERO);
        //RenderSystem.shadeModel(GL11.GL_SMOOTH);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex((double) x + 1, y + 1 + 50, 1).color(0f, 0f, 0f, 1f).endVertex();
        buffer.vertex(x + 1 + 50, y + 1 + 50, 1).color(0f, 0f, 0f, 1f).endVertex();
        buffer.vertex(x + 1 + 50, (double) y + 1, 1).color(currentColor.getRed() / 255f, currentColor.getGreen() / 255f, currentColor.getBlue() / 255f, 1f).endVertex();
        buffer.vertex((double) x + 1, (double) y + 1, 1).color(1f, 1f, 1f, 1f).endVertex();
        tessellator.end();

        //RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        //RenderSystem.enableAlpha();
        //RenderSystem.enableTexture2D();
    }

    @Override
    protected void handleMouseClick(int mouseX, int mouseY, int mouseButton)
    {
        if(mouseButton != 0)
            return;

        if(GuiHelper.isMouseInside(mouseX, mouseY, xPosition + 1, yPosition + 1, xPosition + 51, yPosition + 51))
        {
            colorPicker.setValue(GLHelper.getPixel(mouseX, mouseY).getRGB());
        }
    }
}
