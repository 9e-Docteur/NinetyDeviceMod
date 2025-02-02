package com.mrcrayfish.device.object.tiles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.object.Game;
import com.mrcrayfish.device.object.Game.Layer;
import org.lwjgl.opengl.GL11;

public class TileBlock extends Tile
{
	public TileBlock(int id, int x, int y)
	{
		super(id, x, y);
	}
	
	public TileBlock(int id, int x, int y, int topX, int topY)
	{
		super(id, x, y, topX, topY);
	}
	
	@Override
	public void render(Game game, int x, int y, Layer layer)
	{	
		if(layer == Layer.BACKGROUND)
		{
			super.render(game, x, y, layer);
			if(!game.isFullTile(layer, x, y + 1) && this != Tile.water)
			{
				RenderSystem.setShaderColor(0.6F, 0.6F, 0.6F, 1F);
				RenderUtil.fillWithTexture(game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT + 6, layer.zLevel, this.x * 16, this.y * 16, WIDTH, 2, 16, 4);
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			}
			return;
		}
		
		if(game.getTile(layer.up(), x, y - 1) != this || layer == Layer.FOREGROUND)
		{
			RenderUtil.fillWithTexture(game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT - 6, layer.zLevel, this.topX * 16, this.topY * 16, WIDTH, HEIGHT, 16, 16);
		}
		
		RenderSystem.setShaderColor(0.6F, 0.6F, 0.6F, 1F);
		RenderUtil.fillWithTexture(game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT, layer.zLevel, this.x * 16, this.y * 16, WIDTH, 6, 16, 16);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@Override
	public void renderForeground(Game game, int x, int y, Layer layer)
	{
		if(layer != Layer.BACKGROUND || this == Tile.water)
			return;
		
		Tile tileDown = game.getTile(layer, x, y + 1);
		if(game.getTile(layer, x, y + 1) == Tile.water)
		{
			RenderSystem.setShaderColor(0.6F, 0.6F, 0.6F, 1F);
			RenderUtil.fillWithTexture(game.xPosition + x * Tile.WIDTH, game.yPosition + y * Tile.HEIGHT + 6, layer.zLevel, this.x * 16, this.y * 16, WIDTH, 1, 16, 2);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
