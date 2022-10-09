package be.ninedocteur.ndm.client;

import com.mrcrayfish.device.init.DeviceTileEntites;
import com.mrcrayfish.device.tileentity.render.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileRenders {

    @OnlyIn(Dist.CLIENT)
    public static void register() {
        BlockEntityRenderers.register(DeviceTileEntites.LAPTOP.get(), LaptopRenderer::new);
        BlockEntityRenderers.register(DeviceTileEntites.CHAIR.get(), OfficeChairRenderer::new);
        BlockEntityRenderers.register(DeviceTileEntites.PAPER.get(), PaperRenderer::new);
        BlockEntityRenderers.register(DeviceTileEntites.PRINTER.get(), PrinterRenderer::new);
        BlockEntityRenderers.register(DeviceTileEntites.ROUTER.get(), RouterRenderer::new);

    }

}
