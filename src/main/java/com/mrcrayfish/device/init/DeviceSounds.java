package com.mrcrayfish.device.init;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Author: MrCrayfish
 */
public class DeviceSounds
{
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "ndm");

    public static final RegistryObject<SoundEvent> PRINTER_PRINTING = SOUNDS.register("printer_printing", () -> new SoundEvent(new ResourceLocation("ndm", "printer_printing")));
    public static final RegistryObject<SoundEvent> PRINTER_LOADING_PAPER  = SOUNDS.register("printer_loading_paper", () -> new SoundEvent(new ResourceLocation("ndm", "printer_loading_paper")));

    public static void registerSounds(IEventBus eventBus){
        SOUNDS.register(eventBus);
    }
}
