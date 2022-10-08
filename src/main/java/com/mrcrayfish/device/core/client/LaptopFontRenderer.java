package com.mrcrayfish.device.core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

/**
 * Author: MrCrayfish
 */
public class LaptopFontRenderer extends Font
{
    private boolean debug = false;

    public LaptopFontRenderer(Minecraft minecraft)
    {
        super(res -> new LaptopFontSet(minecraft.getTextureManager(), new ResourceLocation("ndm", "laptop")), false);
        // super(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.getTextureManager(), false);
        //this.onResourceManagerReload(null);
    }

//    @Override
//    public int getCharWidth(char c)
//    {
//        switch(c)
//        {
//            case '\n': return 0;
//            case '\t': return 20;
//        }
//        return super.getCharWidth(c);
//    }
//
//    @Override
//    protected float renderUnicodeChar(char c, boolean italic)
//    {
//        if(debug && (c == '\n' || c == '\t'))
//        {
//            super.renderUnicodeChar(c, italic);
//        }
//        switch(c)
//        {
//            case '\n': return 0F;
//            case '\t': return 20F;
//        }
//        return super.renderUnicodeChar(c, italic);
//    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public static class LaptopFontSet extends FontSet {
        public LaptopFontSet(TextureManager textureManager, ResourceLocation resourceLocation) {
            super(textureManager, resourceLocation);
        }
    }
}
