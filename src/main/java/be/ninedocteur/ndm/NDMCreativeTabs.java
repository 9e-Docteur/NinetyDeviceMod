package be.ninedocteur.ndm;

import com.mrcrayfish.device.init.DeviceBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class NDMCreativeTabs {
    public static final CreativeModeTab NDM = new CreativeModeTab("ndm") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(DeviceBlocks.RED_LAPTOP.get());
        }
    };

}
