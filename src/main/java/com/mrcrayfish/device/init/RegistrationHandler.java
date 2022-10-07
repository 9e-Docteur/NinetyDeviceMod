package com.mrcrayfish.device.init;

import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Author: MrCrayfish
 */
public class RegistrationHandler
{

    public static void register(IEventBus eventBus)
    {
        DeviceBlocks.register(eventBus);
        DeviceItems.register(eventBus);
        DeviceTileEntites.register(eventBus);
    }
}
