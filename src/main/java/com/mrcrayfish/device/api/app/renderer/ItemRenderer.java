package com.mrcrayfish.device.api.app.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;

/**
 * Author: MrCrayfish
 */
public abstract class ItemRenderer<E>
{
    public abstract void render(PoseStack poseStack, E e, Screen gui, Minecraft mc, int x, int y, int width, int height);
}
