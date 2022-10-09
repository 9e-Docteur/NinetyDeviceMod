package com.mrcrayfish.device;

import be.ninedocteur.ndm.utils.NDMUtils;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.print.PrintingManager;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.core.io.task.*;
import com.mrcrayfish.device.core.network.task.TaskConnect;
import com.mrcrayfish.device.core.network.task.TaskGetDevices;
import com.mrcrayfish.device.core.network.task.TaskPing;
import com.mrcrayfish.device.core.print.task.TaskPrint;
import com.mrcrayfish.device.core.task.TaskInstallApp;
import com.mrcrayfish.device.event.BankEvents;
import com.mrcrayfish.device.event.EmailEvents;
import com.mrcrayfish.device.init.DeviceTileEntites;
import com.mrcrayfish.device.init.RegistrationHandler;
import com.mrcrayfish.device.network.PacketHandler;
import com.mrcrayfish.device.object.AppInfo;
import com.mrcrayfish.device.programs.*;
import com.mrcrayfish.device.programs.auction.ApplicationMineBay;
import com.mrcrayfish.device.programs.auction.task.TaskAddAuction;
import com.mrcrayfish.device.programs.auction.task.TaskBuyItem;
import com.mrcrayfish.device.programs.auction.task.TaskGetAuctions;
import com.mrcrayfish.device.programs.debug.ApplicationTextArea;
import com.mrcrayfish.device.programs.email.ApplicationEmail;
import com.mrcrayfish.device.programs.email.task.*;
import com.mrcrayfish.device.programs.example.ApplicationExample;
import com.mrcrayfish.device.programs.example.task.TaskNotificationTest;
import com.mrcrayfish.device.programs.gitweb.ApplicationGitWeb;
import com.mrcrayfish.device.programs.system.ApplicationAppStore;
import com.mrcrayfish.device.programs.system.ApplicationBank;
import com.mrcrayfish.device.programs.system.ApplicationFileBrowser;
import com.mrcrayfish.device.programs.system.ApplicationSettings;
import com.mrcrayfish.device.programs.system.task.*;
import com.mrcrayfish.device.proxy.CommonProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod(Reference.MOD_ID)
public class MrCrayfishDeviceMod 
{

	public static CommonProxy proxy;
	private static final Logger logger = LogManager.getLogger();

	public static final boolean DEVELOPER_MODE = NDMUtils.Launch.isRunningInDev();
	static List<AppInfo> allowedApps;

	public MrCrayfishDeviceMod() {
		proxy = new CommonProxy();
		proxy.preInit();
		proxy.postInit();
		proxy.init();
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		MinecraftForge.EVENT_BUS.register(new DeviceConfig());
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, DeviceConfig.CONFIG, "ndm-config-client.toml");
		DeviceConfig.init();
		MinecraftForge.EVENT_BUS.register(this);
		eventBus.addListener(MrCrayfishDeviceMod::init);
		eventBus.addListener(MrCrayfishDeviceMod::initClientEvent);
		//MinecraftForge.EVENT_BUS.addListener(MrCrayfishDeviceMod::preInit);
		preInit();
		PacketHandler.init();
	}

	public static void preInit()
	{

		//DeviceConfig.load(event.getSuggestedConfigurationFile());

		//RegistrationHandler.init();


	}
	public static void setAllowedApps(List<AppInfo> allowedApps) {
		MrCrayfishDeviceMod.allowedApps = allowedApps;
	}
	

	public static void init(FMLCommonSetupEvent event)
	{
		//MrCrayfishDeviceMod.preInit();


		//RegistrationHandler.init();

		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

		/* Tile Entity Registering */
		//DeviceTileEntites.register();

		//EntityRegistry.registerModEntity(new ResourceLocation("ndm:seat"), EntitySeat.class, "Seat", 0, this, 80, 1, false);

		proxy.preInit();

		/* Packet Registering */

		RegistrationHandler.register(eventBus);

		//NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		MinecraftForge.EVENT_BUS.register(new EmailEvents());
		MinecraftForge.EVENT_BUS.register(new BankEvents());

		registerApplications();

		proxy.postInit();
		proxy.init();
	}

	public static void initClientEvent(FMLClientSetupEvent event){
		DeviceConfig.restore();
	}

	private static void registerApplications()
	{
		// Applications (Both)
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "settings"), ApplicationSettings.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "bank"), ApplicationBank.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "file_browser"), ApplicationFileBrowser.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "gitweb"), ApplicationGitWeb.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "note_stash"), ApplicationNoteStash.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "pixel_painter"), ApplicationPixelPainter.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "ender_mail"), ApplicationEmail.class);
		ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "app_store"), ApplicationAppStore.class);

		// Core
		TaskManager.registerTask(TaskInstallApp.class);
		TaskManager.registerTask(TaskUpdateApplicationData.class);
		TaskManager.registerTask(TaskPrint.class);
		TaskManager.registerTask(TaskUpdateSystemData.class);
		TaskManager.registerTask(TaskConnect.class);
		TaskManager.registerTask(TaskPing.class);
		TaskManager.registerTask(TaskGetDevices.class);

		//Bank
		TaskManager.registerTask(TaskDeposit.class);
		TaskManager.registerTask(TaskWithdraw.class);
		TaskManager.registerTask(TaskGetBalance.class);
		TaskManager.registerTask(TaskPay.class);
		TaskManager.registerTask(TaskAdd.class);
		TaskManager.registerTask(TaskRemove.class);

		//File browser
		TaskManager.registerTask(TaskSendAction.class);
		TaskManager.registerTask(TaskSetupFileBrowser.class);
		TaskManager.registerTask(TaskGetFiles.class);
		TaskManager.registerTask(TaskGetStructure.class);
		TaskManager.registerTask(TaskGetMainDrive.class);

		//Ender Mail
		TaskManager.registerTask(TaskUpdateInbox.class);
		TaskManager.registerTask(TaskSendEmail.class);
		TaskManager.registerTask(TaskCheckEmailAccount.class);
		TaskManager.registerTask(TaskRegisterEmailAccount.class);
		TaskManager.registerTask(TaskDeleteEmail.class);
		TaskManager.registerTask(TaskViewEmail.class);

		if(!DEVELOPER_MODE)
		{
			// Applications (Normal)
			ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "boat_racers"), ApplicationBoatRacers.class);
			ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "mine_bay"), ApplicationMineBay.class);

			// Tasks (Normal)
			TaskManager.registerTask(TaskAddAuction.class);
			TaskManager.registerTask(TaskGetAuctions.class);
			TaskManager.registerTask(TaskBuyItem.class);
		}
		else
		{
			// Applications (Developers)
			ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "example"), ApplicationExample.class);
			ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "icons"), ApplicationIcons.class);
			ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "text_area"), ApplicationTextArea.class);
			ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "test"), ApplicationTest.class);

			TaskManager.registerTask(TaskNotificationTest.class);
		}

		PrintingManager.registerPrint(new ResourceLocation(Reference.MOD_ID, "picture"), ApplicationPixelPainter.PicturePrint.class);
	}

	public static Logger getLogger()
	{
		return logger;
	}
}
