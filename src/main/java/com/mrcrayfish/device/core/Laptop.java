package com.mrcrayfish.device.core;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.System;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.io.Drive;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.task.Callback;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.client.LaptopFontRenderer;
import com.mrcrayfish.device.core.task.TaskInstallApp;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.system.SystemApplication;
import com.mrcrayfish.device.programs.system.component.FileBrowser;
import com.mrcrayfish.device.programs.system.task.TaskUpdateApplicationData;
import com.mrcrayfish.device.programs.system.task.TaskUpdateSystemData;
import com.mrcrayfish.device.tileentity.TileEntityLaptop;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.Color;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

import static com.mrcrayfish.device.util.GuiHelper.isMouseInside;

//TODO Intro message (created by mrcrayfish, donate here)

public class Laptop extends Screen implements System
{
	public static final int ID = 1;
	
	private static final ResourceLocation LAPTOP_GUI = new ResourceLocation(Reference.MOD_ID, "textures/gui/laptop.png");

	public static final ResourceLocation ICON_TEXTURES = new ResourceLocation(Reference.MOD_ID, "textures/atlas/app_icons.png");
	public static final int ICON_SIZE = 14;

	public static final Font fontRenderer = new LaptopFontRenderer(Minecraft.getInstance());

	private static final List<Application> APPLICATIONS = new ArrayList<>();
	private static final List<ResourceLocation> WALLPAPERS = new ArrayList<>();

	private static final int BORDER = 10;
	private static final int DEVICE_WIDTH = 384;
	private static final int DEVICE_HEIGHT = 216;
	static final int SCREEN_WIDTH = DEVICE_WIDTH - BORDER * 2;
	static final int SCREEN_HEIGHT = DEVICE_HEIGHT - BORDER * 2;

	private static System system;
	private static BlockPos pos;
	private static Drive mainDrive;

	private final Settings settings;
	private final TaskBar bar;
	private final Window[] windows;
	private Layout context = null;

	private final CompoundTag appData;
	private final CompoundTag systemData;

	private int currentWallpaper;
	private int lastMouseX, lastMouseY;
	private boolean dragging = false;

	protected List<AppInfo> installedApps = new ArrayList<>();

	public Laptop(TileEntityLaptop laptop)
	{
		super(Component.empty());
		this.appData = laptop.getApplicationData();
		this.systemData = laptop.getSystemData();
		this.windows = new Window[5];
		this.settings = Settings.fromTag(systemData.getCompound("Settings"));
		this.bar = new TaskBar(this);
		this.currentWallpaper = systemData.getInt("CurrentWallpaper");
		if(currentWallpaper < 0 || currentWallpaper >= WALLPAPERS.size()) {
			this.currentWallpaper = 0;
		}
		Laptop.system = this;
		Laptop.pos = laptop.getBlockPos();
	}

	/**
	 * Returns the position of the laptop the player is currently using. This method can ONLY be
	 * called when the laptop GUI is open, otherwise it will return a null position.
	 *
	 * @return the position of the laptop currently in use
	 */
	@Nullable
	public static BlockPos getPos()
	{
		return pos;
	}

	@Override
	protected void init() {
		super.init();
		Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(true);
		int posX = (width - DEVICE_WIDTH) / 2;
		int posY = (height - DEVICE_HEIGHT) / 2;
		bar.init(posX + BORDER, posY + DEVICE_HEIGHT - 28);

		installedApps.clear();
		ListTag tagList = systemData.getList("InstalledApps", Tag.TAG_STRING);
		for(int i = 0; i < tagList.size(); i++)
		{
			AppInfo info = ApplicationManager.getApplication(tagList.getString(i));
			if(info != null)
			{
				installedApps.add(info);
			}
		}
		installedApps.sort(AppInfo.SORT_NAME);
	}

	@Override
	public void onClose() {
		super.onClose();
		Minecraft.getInstance().keyboardHandler.setSendRepeatsToGui(false);
		/* Close all windows and sendTask application data */
		for(Window window : windows)
		{
			if(window != null)
			{
				window.close();
			}
		}

		/* Send system data */
		this.updateSystemData();

		Laptop.pos = null;
		Laptop.system = null;
		Laptop.mainDrive = null;
	}

    private void updateSystemData()
	{
		systemData.putInt("CurrentWallpaper", currentWallpaper);
		systemData.put("Settings", settings.toTag());

		ListTag tagListApps = new ListTag();
		installedApps.forEach(info -> tagListApps.add(StringTag.valueOf(info.getFormattedId())));
		systemData.put("InstalledApps", tagListApps);

		TaskManager.sendTask(new TaskUpdateSystemData(pos, systemData));
	}

	@Override
	public void resize(Minecraft p_96575_, int p_96576_, int p_96577_) {
		super.resize(p_96575_, p_96576_, p_96577_);
		for(Window window : windows)
		{
			if(window != null)
			{
				window.content.markForLayoutUpdate();
			}
		}
	}

	@Override
	public void tick() {
		super.tick();
		bar.onTick();

		for(Window window : windows)
		{
			if(window != null)
			{
				window.onTick();
			}
		}

		FileBrowser.refreshList = false;
	}

	@Override
	public void render(PoseStack poseStack, final int mouseX, final int mouseY, float partialTicks) {
		super.render(poseStack, mouseX, mouseY, partialTicks);
		//Fixes the strange partialTicks that Forge decided to give us
		Minecraft mc = Minecraft.getInstance();

		//this.drawDefaultBackground();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindForSetup(LAPTOP_GUI);

		/* Physical Screen */
		int posX = (width - DEVICE_WIDTH) / 2;
		int posY = (height - DEVICE_HEIGHT) / 2;

		/* Corners */
		this.blit(poseStack, posX, posY, 0, 0, BORDER, BORDER); // TOP-LEFT
		this.blit(poseStack, posX + DEVICE_WIDTH - BORDER, posY, 11, 0, BORDER, BORDER); // TOP-RIGHT
		this.blit(poseStack, posX + DEVICE_WIDTH - BORDER, posY + DEVICE_HEIGHT - BORDER, 11, 11, BORDER, BORDER); // BOTTOM-RIGHT
		this.blit(poseStack, posX, posY + DEVICE_HEIGHT - BORDER, 0, 11, BORDER, BORDER); // BOTTOM-LEFT

		/* Edges */
		RenderUtil.fillWithTexture(posX + BORDER, posY, 10, 0, SCREEN_WIDTH, BORDER, 1, BORDER); // TOP
		RenderUtil.fillWithTexture(posX + DEVICE_WIDTH - BORDER, posY + BORDER, 11, 10, BORDER, SCREEN_HEIGHT, BORDER, 1); // RIGHT
		RenderUtil.fillWithTexture(posX + BORDER, posY + DEVICE_HEIGHT - BORDER, 10, 11, SCREEN_WIDTH, BORDER, 1, BORDER); // BOTTOM
		RenderUtil.fillWithTexture(posX, posY + BORDER, 0, 11, BORDER, SCREEN_HEIGHT, BORDER, 1); // LEFT

		/* Center */
		RenderUtil.fillWithTexture(posX + BORDER, posY + BORDER, 10, 10, SCREEN_WIDTH, SCREEN_HEIGHT, 1, 1);

		/* Wallpaper */
		mc.getTextureManager().bindForSetup(WALLPAPERS.get(currentWallpaper));
		RenderUtil.fillWithFullTexture(posX + 10, posY + 10, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		if(!MrCrayfishDeviceMod.DEVELOPER_MODE)
		{
			drawString(poseStack, fontRenderer, "Alpha v" + Reference.VERSION, posX + BORDER + 5, posY + BORDER + 5, Color.WHITE.getRGB());
		}
		else
		{
			drawString(poseStack, fontRenderer, "Developer Version - " + Reference.VERSION, posX + BORDER + 5, posY + BORDER + 5, Color.WHITE.getRGB());
		}

		boolean insideContext = false;
		if(context != null)
		{
			insideContext = isMouseInside(mouseX, mouseY, context.xPosition, context.yPosition, context.xPosition + context.width, context.yPosition + context.height);
		}

		Image.CACHE.forEach((s, cachedImage) -> cachedImage.delete());

		/* Window */
		for(int i = windows.length - 1; i >= 0; i--)
		{
			Window window = windows[i];
			if(window != null)
			{
				window.render(poseStack, window.laptop, mc, posX + BORDER, posY + BORDER, mouseX, mouseY, i == 0 && !insideContext, partialTicks);
			}
		}

		/* Application Bar */
		Laptop laptop = this;
		bar.render(poseStack, laptop,  mc, posX + 10, posY + DEVICE_HEIGHT - 28, mouseX, mouseY, partialTicks);

		if(context != null)
		{
			context.render(poseStack, this,  mc, context.xPosition, context.yPosition, mouseX, mouseY, true, partialTicks);
		}

		Image.CACHE.entrySet().removeIf(entry ->
		{
			Image.CachedImage cachedImage = entry.getValue();
			if(cachedImage.isDynamic() && cachedImage.isPendingDeletion())
			{
				int texture = cachedImage.getTextureId();
				if(texture != -1)
				{
					GL11.glDeleteTextures(texture);
				}
				return true;
			}
			return false;
		});

	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		this.lastMouseX = (int) mouseX;
		this.lastMouseY = (int) mouseY;

		int posX = (width - SCREEN_WIDTH) / 2;
		int posY = (height - SCREEN_HEIGHT) / 2;

		if(this.context != null)
		{
			int dropdownX = context.xPosition;
			int dropdownY = context.yPosition;
			if(isMouseInside((int) mouseX, (int) mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height))
			{
				this.context.handleMouseClick((int)mouseX, (int)mouseY, mouseButton);
				return false;
			}
			else
			{
				this.context = null;
			}
		}

		this.bar.handleClick(this, posX, posY + SCREEN_HEIGHT - TaskBar.BAR_HEIGHT, (int)mouseX,(int) mouseY, mouseButton);

		for(int i = 0; i < windows.length; i++)
		{
			Window<Application> window = windows[i];
			if(window != null)
			{
				Window<Dialog> dialogWindow = window.getContent().getActiveDialog();
				if(isMouseWithinWindow((int)mouseX, (int)mouseY, window) || isMouseWithinWindow((int)mouseX, (int)mouseY, dialogWindow))
				{
					windows[i] = null;
					updateWindowStack();
					windows[0] = window;

					windows[0].handleMouseClick(this, posX, posY, (int)mouseX, (int)mouseY, mouseButton);

					if(isMouseWithinWindowBar((int)mouseX, (int)mouseY, dialogWindow))
					{
						this.dragging = true;
						return false;
					}

					if(isMouseWithinWindowBar((int)mouseX, (int)mouseY, window) && dialogWindow == null)
					{
						this.dragging = true;
						return false;
					}
					break;
				}
			}
		}
		return super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int state)
	{
		super.mouseReleased(mouseX, mouseY, state);
		this.dragging = false;
		if(this.context != null)
		{
			int dropdownX = context.xPosition;
			int dropdownY = context.yPosition;
			if(isMouseInside((int) mouseX,(int) mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height))
			{
				this.context.handleMouseRelease((int)mouseX, (int)mouseY, state);
			}
		}
		else if(windows[0] != null)
		{
			windows[0].handleMouseRelease((int)mouseX, (int)mouseY, state);
		}
		return true;
	}
	
//	@Override
//	public void handleKeyboardInput() throws IOException
//    {
//        if (Keyboard.getEventKeyState())
//        {
//        	char pressed = Keyboard.getEventCharacter();
//        	int code = Keyboard.getEventKey();
//
//            if(windows[0] != null)
//    		{
//    			windows[0].handleKeyTyped(pressed, code);
//    		}
//
//            super.keyTyped(pressed, code);
//        }
//        else
//        {
//        	if(windows[0] != null)
//    		{
//    			windows[0].handleKeyReleased(Keyboard.getEventCharacter(), Keyboard.getEventKey());
//    		}
//        }
//
//        this.mc.dispatchKeypresses();
//    }

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		int posX = (width - SCREEN_WIDTH) / 2;
		int posY = (height - SCREEN_HEIGHT) / 2;

		try {
			if (this.context != null) {
				int dropdownX = context.xPosition;
				int dropdownY = context.yPosition;
				if (isMouseInside((int) mouseX, (int) mouseY, dropdownX, dropdownY, dropdownX + context.width, dropdownY + context.height)) {
					this.context.handleMouseDrag((int) mouseX, (int) mouseY, button);
				}
				return true;
			}

			if (windows[0] != null) {
				Window<Application> window = (Window<Application>) windows[0];
				Window<Dialog> dialogWindow = window.getContent().getActiveDialog();
				if (dragging) {
					if (isMouseOnScreen((int) mouseX, (int) mouseY)) {
						Objects.requireNonNull(dialogWindow, (Supplier<String>) window).handleWindowMove(posX, posY, (int) -(lastMouseX - mouseX), (int) -(lastMouseY - mouseY));
					} else {
						dragging = false;
					}
				} else {
					if (isMouseWithinWindow((int) mouseX, (int) mouseY, window) || isMouseWithinWindow((int) mouseX, (int) mouseY, dialogWindow)) {
						window.handleMouseDrag((int) mouseX, (int) mouseY, button);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
			message.setTitle("Error");
			windows[0].openDialog(message);
		}
		this.lastMouseX = (int) mouseX;
		this.lastMouseY = (int) mouseY;
		return true;
	}


//	@Override
//	public void handleMouseInput() throws IOException
//	{
//		super.handleMouseInput();
//		int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
//        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
//		int scroll = Mouse.getEventDWheel();
//		if(scroll != 0)
//		{
//			if(windows[0] != null)
//			{
//				windows[0].handleMouseScroll(mouseX, mouseY, scroll >= 0);
//			}
//		}
//	}
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
		if (delta != 0) {
			try {
				if (windows[0] != null) {
					windows[0].handleMouseScroll((int) mouseX, (int) mouseY, delta >= 0);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Dialog.Message message = new Dialog.Message("An error has occurred.\nSend logs to devs.");
				message.setTitle("Error");
				windows[0].openDialog(message);
			}
		}
		return true;
	}

	@Override
	public void renderComponentTooltip(@NotNull PoseStack pose, @NotNull List<Component> tooltips, int x, int y) {
		super.renderComponentTooltip(pose, tooltips, x, y);
	}
	public boolean sendApplicationToFront(AppInfo info)
	{
		for(int i = 0; i < windows.length; i++)
		{
			Window window = windows[i];
			if(window != null && window.content instanceof Application && ((Application) window.content).getInfo() == info)
			{
				windows[i] = null;
				updateWindowStack();
				windows[0] = window;
				return true;
			}
		}
		return false;
	}

	@Override
	public void openApplication(AppInfo info)
	{
		openApplication(info, (CompoundTag) null);
	}

	@Override
	public void openApplication(AppInfo info, CompoundTag intentTag)
	{
		Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
		optional.ifPresent(application -> openApplication(application, intentTag));
	}

	private void openApplication(Application app, CompoundTag intent)
	{
		if(!isApplicationInstalled(app.getInfo()))
			return;

		if(!isValidApplication(app.getInfo()))
			return;

		if(sendApplicationToFront(app.getInfo()))
			return;

		Window<Application> window = new Window<>(app, this);
		window.init((width - SCREEN_WIDTH) / 2, (height - SCREEN_HEIGHT) / 2, intent);

		if(appData.contains(app.getInfo().getFormattedId()))
		{
			app.load(appData.getCompound(app.getInfo().getFormattedId()));
		}

		if(app instanceof SystemApplication)
		{
			((SystemApplication) app).setLaptop(this);
		}

		if(app.getCurrentLayout() == null)
		{
			app.restoreDefaultLayout();
		}
		
		addWindow(window);

	    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	@Override
	public boolean openApplication(AppInfo info, File file)
	{
		if(!isApplicationInstalled(info))
			return false;

		if(!isValidApplication(info))
			return false;

		Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
		if(optional.isPresent())
		{
			Application application = optional.get();
			boolean alreadyRunning = isApplicationRunning(info);
			openApplication(application, null);
			if(isApplicationRunning(info))
			{
				if(!application.handleFile(file))
				{
					if(!alreadyRunning)
					{
						closeApplication(application);
					}
					return false;
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void closeApplication(AppInfo info)
	{
		Optional<Application> optional = APPLICATIONS.stream().filter(app -> app.getInfo() == info).findFirst();
		optional.ifPresent(this::closeApplication);
	}

	private void closeApplication(Application app)
	{
		for(int i = 0; i < windows.length; i++)
		{
			Window<Application> window = windows[i];
			if(window != null)
			{
				if(window.content.getInfo().equals(app.getInfo()))
				{
					if(app.isDirty())
					{
						CompoundTag container = new CompoundTag();
						app.save(container);
						app.clean();
						appData.put(app.getInfo().getFormattedId(), container);
						TaskManager.sendTask(new TaskUpdateApplicationData(pos.getX(), pos.getY(), pos.getZ(), app.getInfo().getFormattedId(), container));
					}

					if(app instanceof SystemApplication)
					{
						((SystemApplication) app).setLaptop(null);
					}

					window.handleClose();
					windows[i] = null;
					return;
				}
			}
		}
	}
	
	private void addWindow(Window<Application> window)
	{
		if(hasReachedWindowLimit())
			return;

		updateWindowStack();
		windows[0] = window;
	}

	private void updateWindowStack()
	{
		for(int i = windows.length - 1; i >= 0; i--)
		{
			if(windows[i] != null)
			{
				if(i + 1 < windows.length)
				{
					if(i == 0 || windows[i - 1] != null)
					{
						if(windows[i + 1] == null)
						{
							windows[i + 1] = windows[i];
							windows[i] = null;
						}
					}
				}
			}
		}
	}

	private boolean hasReachedWindowLimit()
	{
		for(Window window : windows)
		{
			if(window == null) return false;
		}
		return true;
	}

	private boolean isMouseOnScreen(int mouseX, int mouseY)
	{
		int posX = (width - SCREEN_WIDTH) / 2;
		int posY = (height - SCREEN_HEIGHT) / 2;
		return isMouseInside(mouseX, mouseY, posX, posY, posX + SCREEN_WIDTH, posY + SCREEN_HEIGHT);
	}

	private boolean isMouseWithinWindowBar(int mouseX, int mouseY, Window window)
	{
		if(window == null) return false;
		int posX = (width - SCREEN_WIDTH) / 2;
		int posY = (height - SCREEN_HEIGHT) / 2;
		return isMouseInside(mouseX, mouseY, posX + window.offsetX + 1, posY + window.offsetY + 1, posX + window.offsetX + window.width - 13, posY + window.offsetY + 11);
	}

	private boolean isMouseWithinWindow(int mouseX, int mouseY, Window window)
	{
		if(window == null) return false;
		int posX = (width - SCREEN_WIDTH) / 2;
		int posY = (height - SCREEN_HEIGHT) / 2;
		return isMouseInside(mouseX, mouseY, posX + window.offsetX, posY + window.offsetY, posX + window.offsetX + window.width, posY + window.offsetY + window.height);
	}
	
	public boolean isMouseWithinApp(int mouseX, int mouseY, Window window)
	{
		int posX = (width - SCREEN_WIDTH) / 2;
		int posY = (height - SCREEN_HEIGHT) / 2;
		return isMouseInside(mouseX, mouseY, posX + window.offsetX + 1, posY + window.offsetY + 13, posX + window.offsetX + window.width - 1, posY + window.offsetY + window.height - 1);
	}

	public boolean isApplicationRunning(AppInfo info)
	{
		for(Window window : windows) 
		{
			if(window != null && ((Application) window.content).getInfo() == info)
			{
				return true;
			}
		}
		return false;
	}

	public void nextWallpaper()
	{
		if(currentWallpaper + 1 < WALLPAPERS.size())
		{
			currentWallpaper++;
		}
	}
	
	public void prevWallpaper()
	{
		if(currentWallpaper - 1 >= 0)
		{
			currentWallpaper--;
		}
	}

	public int getCurrentWallpaper()
	{
		return currentWallpaper;
	}

	public static void addWallpaper(ResourceLocation wallpaper)
	{
		if(wallpaper != null)
		{
			WALLPAPERS.add(wallpaper);
		}
	}

	public List<ResourceLocation> getWallapapers()
	{
		return ImmutableList.copyOf(WALLPAPERS);
	}

	@Nullable
	public Application getApplication(String appId)
	{
		return APPLICATIONS.stream().filter(app -> app.getInfo().getFormattedId().equals(appId)).findFirst().orElse(null);
	}

	@Override
	public List<AppInfo> getInstalledApplications()
	{
		return ImmutableList.copyOf(installedApps);
	}

	public boolean isApplicationInstalled(AppInfo info)
	{
		return info.isSystemApp() || installedApps.contains(info);
	}

	private boolean isValidApplication(AppInfo info)
	{
		if(MrCrayfishDeviceMod.proxy.hasAllowedApplications())
		{
			return MrCrayfishDeviceMod.proxy.getAllowedApplications().contains(info);
		}
		return true;
	}

	public void installApplication(AppInfo info, @Nullable Callback<Object> callback)
	{
		if(!isValidApplication(info))
			return;

		Task task = new TaskInstallApp(info, pos, true);
		task.setCallback((tagCompound, success) ->
		{
            if(success)
			{
				installedApps.add(info);
				installedApps.sort(AppInfo.SORT_NAME);
			}
			if(callback != null)
			{
				callback.execute(null, success);
			}
        });
		TaskManager.sendTask(task);
	}

	public void removeApplication(AppInfo info, @Nullable Callback<Object> callback)
	{
		if(!isValidApplication(info))
			return;

		Task task = new TaskInstallApp(info, pos, false);
		task.setCallback((tagCompound, success) ->
		{
			if(success)
			{
				installedApps.remove(info);
			}
			if(callback != null)
			{
				callback.execute(null, success);
			}
		});
		TaskManager.sendTask(task);
	}

	public static System getSystem()
	{
		return system;
	}

	public static void setMainDrive(Drive mainDrive)
	{
		if(Laptop.mainDrive == null)
		{
			Laptop.mainDrive = mainDrive;
		}
	}

	@Nullable
	public static Drive getMainDrive()
	{
		return mainDrive;
	}

	public List<Application> getApplications()
	{
		return APPLICATIONS;
	}

	public TaskBar getTaskBar()
	{
		return bar;
	}

	public Settings getSettings()
	{
		return settings;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void openContext(Layout layout, int x, int y)
	{
		layout.updateComponents(x, y);
		context = layout;
		layout.init();
	}

	@Override
	public boolean hasContext()
	{
		return context != null;
	}

	@Override
	public void closeContext()
	{
		context = null;
		dragging = false;
	}

	public static Font getFontRenderer(){
		return fontRenderer;
	}
}
