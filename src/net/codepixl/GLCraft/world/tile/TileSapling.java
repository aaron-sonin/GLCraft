package net.codepixl.GLCraft.world.tile;

import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.Chunk;
import net.codepixl.GLCraft.world.WorldManager;

public class TileSapling extends Tile{

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 17;
	}

	@Override
	public Color4f getColor() {
		// TODO Auto-generated method stub
		return Color4f.WHITE;
	}
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Sapling";
	}

	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canPassThrough() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public RenderType getRenderType(){
		return RenderType.CROSS;
	}
	
	@Override
	public void randomTick(int x, int y, int z, WorldManager w){
		if(Constants.rand.nextFloat() < 0.1){
			Chunk.createCustomTree(x, y, z, Tile.Log, Tile.Leaf, w);
		}
	}
}