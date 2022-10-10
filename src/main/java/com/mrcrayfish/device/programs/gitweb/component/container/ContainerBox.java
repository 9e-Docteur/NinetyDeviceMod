package com.mrcrayfish.device.programs.gitweb.component.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public abstract class ContainerBox extends Component
{
    protected static final ResourceLocation CONTAINER_BOXES_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/container_boxes.png");

    public static final int WIDTH = 128;

    protected List<Slot> slots = new ArrayList<>();
    protected int boxU, boxV;
    protected int height;
    protected ItemStack icon;
    protected String title;

    public ContainerBox(int left, int top, int boxU, int boxV, int height, ItemStack icon, String title)
    {
        super(left, top);
        this.boxU = boxU;
        this.boxV = boxV;
        this.height = height;
        this.icon = icon;
        this.title = title;
    }

    @Override
    protected void render(PoseStack poseStack, Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks)
    {
        RenderSystem.setShaderTexture(0, CONTAINER_BOXES_TEXTURE);
        RenderUtil.fillWithTexture(x, y + 12, boxU, boxV, WIDTH, height, WIDTH, height, 256, 256);

        int contentOffset = (WIDTH - (Laptop.fontRenderer.width(title) + 8 + 4)) / 2;
        poseStack.pushPose();
        {
            poseStack.translate(x + contentOffset, y, 0);
            poseStack.scale(0.5f, 0.5f, 0.5f);
            RenderUtil.renderItem(0, 0, icon, false);
        }
        poseStack.popPose();

        RenderUtil.drawStringClipped(title, x + contentOffset + 8 + 4, y, 110, Color.WHITE.getRGB(), true);

        slots.forEach(slot -> slot.render(x, y + 12));
    }

    @Override
    protected void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive)
    {
        slots.forEach(slot -> slot.renderOverlay(laptop, xPosition, yPosition + 12, mouseX, mouseY));
    }

    protected class Slot
    {
        private final int slotX;
        private final int slotY;
        private final ItemStack stack;

        public Slot(int slotX, int slotY, ItemStack stack)
        {
            this.slotX = slotX;
            this.slotY = slotY;
            this.stack = stack;
        }

        public void render(int x, int y)
        {
            RenderUtil.renderItem(x + slotX, y + slotY, stack, true);
        }

        public void renderOverlay(Laptop laptop, int x, int y, int mouseX, int mouseY)
        {
            if(GuiHelper.isMouseWithin(mouseX, mouseY, x + slotX, y + slotY, 16, 16))
            {
                if(!stack.isEmpty())
                {
                    PoseStack poseStack = new PoseStack();
                    //net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
                    laptop.renderTooltip(poseStack, (net.minecraft.network.chat.Component) laptop.getTooltipFromItem(stack), mouseX, mouseY);
                    //net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
                }
            }

//            RenderSystem.disableRescaleNormal();
//            RenderHelper.disableStandardItemLighting();
//            RenderSystem.disableDepth();
        }

        public ItemStack getStack()
        {
            return stack;
        }
    }
}
