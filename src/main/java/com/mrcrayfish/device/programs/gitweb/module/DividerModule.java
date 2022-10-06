package com.mrcrayfish.device.programs.gitweb.module;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.programs.gitweb.component.GitWebFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class DividerModule extends Module
{
    private final PoseStack poseStack = new PoseStack();
    @Override
    public String[] getRequiredData()
    {
        return new String[] { "size" };
    }

    @Override
    public String[] getOptionalData()
    {
        return new String[] { "color" };
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width)
    {
        return Math.max(0, Integer.parseInt(data.get("size")));
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data)
    {
        if(data.containsKey("color"))
        {
            int color = Integer.parseInt(data.get("color"));
            layout.setBackground((poseStack, gui, mc, x, y, width1, height, mouseX, mouseY, windowActive) ->
            {
                Gui.fill(poseStack, x, y, x + width1, y + height, color);
            });
        }
    }
}
