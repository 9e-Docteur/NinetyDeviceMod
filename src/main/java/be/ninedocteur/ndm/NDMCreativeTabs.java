package be.ninedocteur.ndm;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class NDMCreativeTabs {
    public static final CreativeModeTab NDM = new CreativeModeTab("ndm") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(DMBlocks.ZINC_BLOCK.get());
        }
    };

}
