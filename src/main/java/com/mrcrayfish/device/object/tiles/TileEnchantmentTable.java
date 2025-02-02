package com.mrcrayfish.device.object.tiles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.object.Game;
import com.mrcrayfish.device.object.Game.Layer;
import org.lwjgl.opengl.GL11;

public class TileEnchantmentTable extends Tile
{
	public TileEnchantmentTable(int id, int x, int y)
	{
		super(id, x, y);
	}
	
	@Override
	public void render(Game game, int x, int y, Layer layer)
	{
		if(game.getTile(layer.up(), x, y - 1) != this || layer == Layer.FOREGROUND)
		{
			RenderUtil.fillWithTexture(game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT - 4, layer.zLevel, this.topX * 16 + 16, this.topY * 16, WIDTH, HEIGHT, 16, 16);
		}
		
		RenderSystem.setShaderColor(0.6F, 0.6F, 0.6F, 1F);
		RenderUtil.fillWithTexture(game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT + 2, layer.zLevel, this.x * 16, this.y * 16 + 4, WIDTH, 4, 16, 12);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@Override
	public boolean isFullTile()
	{
		return false;
	}
}
