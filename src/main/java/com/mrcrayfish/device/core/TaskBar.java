package com.mrcrayfish.device.core;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.network.TrayItemWifi;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.object.TrayItem;
import com.mrcrayfish.device.programs.system.ApplicationAppStore;
import com.mrcrayfish.device.programs.system.ApplicationFileBrowser;
import com.mrcrayfish.device.programs.system.ApplicationSettings;
import com.mrcrayfish.device.programs.system.SystemApplication;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class TaskBar
{
	public static final ResourceLocation APP_BAR_GUI = new ResourceLocation("ndm:textures/gui/application_bar.png");

	private static final int APPS_DISPLAYED = MrCrayfishDeviceMod.DEVELOPER_MODE ? 18 : 10;
	public static final int BAR_HEIGHT = 18;

	private final Laptop laptop;
	
	private final int offset = 0;
	private final int pingTimer = 0;

	private final List<TrayItem> trayItems = new ArrayList<>();

	public TaskBar(Laptop laptop)
	{
		this.laptop = laptop;
		trayItems.add(new ApplicationFileBrowser.FileBrowserTrayItem());
		trayItems.add(new ApplicationSettings.SettingsTrayItem());
		trayItems.add(new ApplicationAppStore.StoreTrayItem());
		trayItems.add(new TrayItemWifi());
	}

	public void init()
	{
		trayItems.forEach(TrayItem::init);
	}

	public void setupApplications(List<Application> applications)
	{
		final Predicate<Application> VALID_APPS = app ->
		{
			if(app instanceof SystemApplication)
			{
				return true;
			}
			if(MrCrayfishDeviceMod.proxy.hasAllowedApplications())
			{
				if(MrCrayfishDeviceMod.proxy.getAllowedApplications().contains(app.getInfo()))
				{
					return !MrCrayfishDeviceMod.DEVELOPER_MODE || Settings.isShowAllApps();
				}
				return false;
			}
			else if(MrCrayfishDeviceMod.DEVELOPER_MODE)
			{
				return Settings.isShowAllApps();
			}
			return true;
		};
	}

	public void init(int posX, int posY)
	{
		init();
	}

	public void onTick()
	{
		trayItems.forEach(TrayItem::tick);
	}
	
	public void render(PoseStack poseStack, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, float partialTicks)
	{
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.75F);
		RenderSystem.enableBlend();
		RenderSystem.setShaderTexture(0, APP_BAR_GUI);

		Color bgColor = new Color(laptop.getSettings().getColorScheme().getBackgroundColor()).brighter().brighter();
		float[] hsb = Color.RGBtoHSB(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), null);
		bgColor = new Color(Color.HSBtoRGB(hsb[0], hsb[1], 1.0F));
		RenderSystem.setShaderColor(bgColor.getRed() / 255F, bgColor.getGreen() / 255F, bgColor.getBlue() / 255F, 1.0F);

		int trayItemsWidth = trayItems.size() * 14;
		RenderUtil.fillWithTexture(x, y, 0, 0, 1, 18, 1, 18);
		RenderUtil.fillWithTexture(x + 1, y, 1, 0, Laptop.SCREEN_WIDTH - 36 - trayItemsWidth, 18, 1, 18);
		RenderUtil.fillWithTexture(x + Laptop.SCREEN_WIDTH - 35 - trayItemsWidth, y, 2, 0, 35 + trayItemsWidth, 18, 1, 18);

		RenderSystem.disableBlend();
		
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		for(int i = 0; i < APPS_DISPLAYED && i < laptop.installedApps.size(); i++)
		{
			AppInfo info = laptop.installedApps.get(i + offset);
			RenderUtil.drawApplicationIcon(info, x + 2 + i * 16, y + 2);
			if(laptop.isApplicationRunning(info))
			{
				RenderSystem.setShaderTexture(0, APP_BAR_GUI);
				laptop.blit(poseStack, x + 1 + i * 16, y + 1, 35, 0, 16, 16);
			}
		}

		mc.font.draw(poseStack, timeToString(mc.player.level.getGameTime()), x + 334, y + 5, Color.WHITE.getRGB());

		/* Settings App */
		int startX = x + 317;
		for(int i = 0; i < trayItems.size(); i++)
		{
			int posX = startX - (trayItems.size() - 1 - i) * 14;
			if(isMouseInside(mouseX, mouseY, posX, y + 2, posX + 13, y + 15))
			{
				Gui.fill(poseStack, posX, y + 2, posX + 14, y + 16, new Color(1.0F, 1.0F, 1.0F, 0.1F).getRGB());
			}
			trayItems.get(i).getIcon().draw(poseStack, mc, posX + 2, y + 4);
		}

		RenderSystem.setShaderTexture(0, APP_BAR_GUI);

		/* Other Apps */
		if(isMouseInside(mouseX, mouseY, x + 1, y + 1, x + 236, y + 16))
		{
			int appIndex = (mouseX - x - 1) / 16;
			if(appIndex >= 0 && appIndex < offset + APPS_DISPLAYED && appIndex < laptop.installedApps.size())
			{
				laptop.blit(poseStack, x + appIndex * 16 + 1, y + 1, 35, 0, 16, 16);
				laptop.renderTooltip(poseStack, Component.literal(Collections.singletonList(laptop.installedApps.get(appIndex).getName()).toString()), mouseX, mouseY);
			}
		}
		
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		//RenderHelper.disableStandardItemLighting();
	}
	
	public void handleClick(Laptop laptop, int x, int y, int mouseX, int mouseY, int mouseButton) 
	{
		if(isMouseInside(mouseX, mouseY, x + 1, y + 1, x + 236, y + 16))
		{
			int appIndex = (mouseX - x - 1) / 16;
			if(appIndex >= 0 && appIndex <= offset + APPS_DISPLAYED && appIndex < laptop.installedApps.size())
			{
				laptop.openApplication(laptop.installedApps.get(appIndex));
				return;
			}
		}

		int startX = x + 317;
		for(int i = 0; i < trayItems.size(); i++)
		{
			int posX = startX - (trayItems.size() - 1 - i) * 14;
			if(isMouseInside(mouseX, mouseY, posX, y + 2, posX + 13, y + 15))
			{
				trayItems.get(i).handleClick(mouseX, mouseY, mouseButton);
				break;
			}
		}
	}
	
	public boolean isMouseInside(int mouseX, int mouseY, int x1, int y1, int x2, int y2)
	{
		return mouseX >= x1 && mouseX <= x2 && mouseY >= y1 && mouseY <= y2;
	}

	public String timeToString(long time) 
	{
	    int hours = (int) ((Math.floor(time / 1000.0) + 7) % 24);
	    int minutes = (int) Math.floor((time % 1000) / 1000.0 * 60);
	    return String.format("%02d:%02d", hours, minutes);
	}
}
