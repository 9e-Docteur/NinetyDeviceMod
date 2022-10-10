package com.mrcrayfish.device.programs.system.layout;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.core.Laptop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import javax.annotation.Nullable;
import java.awt.Color;

/**
 * Author: MrCrayfish
 */
public class StandardLayout extends Layout
{
    private final String title;
    protected Application app;
    private final Layout previous;
    private IIcon icon;

    public StandardLayout(String title, int width, int height, Application app, @Nullable Layout previous)
    {
        super(width, height);
        this.title = title;
        this.app = app;
        this.previous = previous;
    }

    @Override
    public void init()
    {
        if(previous != null)
        {
            Button btnBack = new Button(2, 2, Icons.ARROW_LEFT);
            btnBack.setClickListener((mouseX, mouseY, mouseButton) ->
            {
                if(mouseButton == 0)
                {
                    app.setCurrentLayout(previous);
                }
            });
            this.addComponent(btnBack);
        }
    }

    @Override
    public void render(PoseStack poseStack, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getHeaderColor());
        Gui.fill(poseStack, x, y, x + width, y + 20, color.getRGB());
        Gui.fill(poseStack, x, y + 20, x + width, y + 21, color.darker().getRGB());

        if(previous == null && icon != null)
        {
            icon.draw(poseStack, mc, x + 5, y + 5);
        }
        mc.font.draw(poseStack, title, x + 5 + (previous != null || icon != null ? 16 : 0), y + 7, Color.WHITE.getRGB());

        super.render(poseStack, laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
    }

    public void setIcon(IIcon icon)
    {
        this.icon = icon;
    }
}
