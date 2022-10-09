package be.ninedocteur.ndm.event;

import be.ninedocteur.ndm.client.ModelRegistry;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.tileentity.render.PrinterRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DeviceEvent {

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void modelRegistryEvent(EntityRenderersEvent.RegisterLayerDefinitions registerLayerDefinitions) {
        registerLayerDefinitions.registerLayerDefinition(ModelRegistry.PAPER, PrinterRenderer.ModelPaper::createBodyLayer);

    }
}
