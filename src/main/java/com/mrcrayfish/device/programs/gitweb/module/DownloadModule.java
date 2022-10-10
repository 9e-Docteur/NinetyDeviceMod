package com.mrcrayfish.device.programs.gitweb.module;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.gitweb.component.GitWebFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.TagParser;
import net.minecraft.nbt.CompoundTag;

import java.awt.*;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class DownloadModule extends Module
{
    private final PoseStack poseStack = new PoseStack();
    @Override
    public String[] getRequiredData()
    {
        return new String[] { "file-app", "file-data" };
    }

    @Override
    public String[] getOptionalData()
    {
        return new String[] { "file-name", "text" };
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width)
    {
        return 45;
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data)
    {
        int height = calculateHeight(data, width) - 5;
        AppInfo info = ApplicationManager.getApplication(data.get("file-app"));
        layout.setBackground((poseStack, gui, mc, x, y, width1, height1, mouseX, mouseY, windowActive) ->
        {
            int section = layout.width / 6;
            int subWidth = section * 4;
            int posX = x + section;
            int posY = y + 5;
            Gui.fill(poseStack, posX, posY, posX + subWidth, posY + height - 5, Color.BLACK.getRGB());
            Gui.fill(poseStack, posX + 1, posY + 1, posX + subWidth - 1, posY + height - 5 - 1, Color.DARK_GRAY.getRGB());

            GlStateManager._clearColor(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getInstance().getTextureManager().bindForSetup(Laptop.ICON_TEXTURES);
            int iconU = 0, iconV = 0;
            if(info != null)
            {
                iconU = info.getIconU();
                iconV = info.getIconV();
            }
            RenderUtil.fillWithTexture(posX + 5, posY + 3, iconU, iconV, 28, 28, 14, 14, 224, 224);

            int textWidth = subWidth - 70 - 10 - 30 - 5;
            RenderUtil.drawStringClipped(poseStack, data.getOrDefault("file-name", "File"), posX + 37, posY + 7, textWidth, Color.ORANGE.getRGB(), true);
            if(data.containsKey("text"))
            {
                RenderUtil.drawStringClipped(poseStack, data.get("text"), posX + 37, posY + 19, textWidth, Color.LIGHT_GRAY.getRGB(), false);
            }
        });

        int section = layout.width / 6;
        Button button = new Button(0, 10, "Download", Icons.IMPORT);
        button.left = section * 5 - 70 - 5;
        button.setSize(70, height - 15);
        button.setClickListener((mouseX, mouseY, mouseButton) ->
        {
            try
            {
                CompoundTag tag = TagParser.parseTag(data.get("file-data"));
                File file = new File(data.getOrDefault("file-name", ""), data.get("file-app"), tag);
                Dialog dialog = new Dialog.SaveFile(frame.getApp(), file);
                frame.getApp().openDialog(dialog);
            }
            catch(CommandSyntaxException e)
            {
                e.printStackTrace();
            }
        });
        layout.addComponent(button);
    }
}
