package be.ninedocteur.ndm.utils;

import net.minecraft.client.Minecraft;

public class NDMUtils {
    public static class Launch{
        public static boolean isRunningInDev() {
            return Minecraft.getInstance().getLaunchedVersion().equalsIgnoreCase("MOD_DEV");
        }
    }
}
