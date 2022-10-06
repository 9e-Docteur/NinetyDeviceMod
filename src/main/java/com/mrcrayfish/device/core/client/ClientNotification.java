package com.mrcrayfish.device.core.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class ClientNotification implements Toast
{
    private static final ResourceLocation TEXTURE_TOASTS = new ResourceLocation("cdm:textures/gui/toast.png");

    private IIcon icon;
    private String title;
    private String subTitle;

    private ClientNotification() {}

    @Override
    public Visibility render(PoseStack poseStack, ToastComponent toastGui, long delta)
    {
        RenderSystem.setShaderColor(1F, 1.0F, 1.0F, 1.0F);
        toastGui.getMinecraft().getTextureManager().bindForSetup(TEXTURE_TOASTS);
        toastGui.blit(poseStack, 0, 0, 0, 0, 160, 32);

        if(subTitle == null)
        {
            toastGui.getMinecraft().font.draw(poseStack, RenderUtil.clipStringToWidth(I18n.get(title), 118), 38, 12, -1);
        }
        else
        {
            toastGui.getMinecraft().font.draw(poseStack, RenderUtil.clipStringToWidth(I18n.get(title), 118), 38, 7, -1);
            toastGui.getMinecraft().font.draw(poseStack, RenderUtil.clipStringToWidth(I18n.get(subTitle), 118), 38, 18, -1);
        }

        toastGui.getMinecraft().getTextureManager().bindForSetup(icon.getIconAsset());
        RenderUtil.fillWithTexture(6, 6, icon.getU(), icon.getV(), icon.getGridWidth(), icon.getGridHeight(), icon.getIconSize(), icon.getIconSize(), icon.getSourceWidth(), icon.getSourceHeight());

        return delta >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    public static ClientNotification loadFromTag(CompoundTag tag)
    {
        ClientNotification notification = new ClientNotification();

        int ordinal = tag.getCompound("icon").getInt("ordinal");
        String className = tag.getCompound("icon").getString("className");

        try
        {
            notification.icon = (IIcon)Class.forName(className).getEnumConstants()[ordinal];
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        notification.title = tag.getString("title");
        if(tag.contains("subTitle", Tag.TAG_STRING))
        {
            notification.subTitle = tag.getString("subTitle");
        }

        return notification;
    }

    public void push()
    {
        Minecraft.getInstance().getToasts().addToast(this);
    }
}
