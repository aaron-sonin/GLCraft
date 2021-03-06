package net.codepixl.GLCraft.world.entity;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagCompound;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.util.AABB;
import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.util.MathUtils;
import net.codepixl.GLCraft.world.Chunk;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.tile.Tile;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;

import static org.lwjgl.opengl.GL11.*;

public class EntityTNT extends EntitySolid {

	public EntityTNT(Vector3f pos, Vector3f rot, Vector3f vel, WorldManager worldManager) {
		super(pos, rot, vel, worldManager);
	}

	public static Entity fromNBT(TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException{
		Vector3f pos = NBTUtil.vecFromList("Pos",t);
		return new EntityTNT(pos, new Vector3f(), new Vector3f(), w);
	}
	
	@Override
	public void render(){
		float col = 0f;
		if(timeAlive % 1000 <= 500){
			col = MathUtils.easeInOutQuad((float)timeAlive/1000f, 0f, 0.3f, 0.5f);
		}else{
			col = MathUtils.easeInOutQuad((float)timeAlive/1000f, 0.3f, -0.3f, 0.5f);
		}
		float size = 1f;
		float offset = 0f;
		if(this.timeAlive > 3500f){
			size += ((this.timeAlive - 3500f)/500f)/3f;
			offset = (size - 1f)/2f;
		}
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_ADD);
		glBegin(GL_QUADS);
		Shape.createCube(this.getX()-0.5f-offset, this.getY(), this.getZ()-0.5f-offset, this.getColor(), Tile.Tnt.getTexCoords(), size);
		glEnd();
		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
	}
	
	@Override
	public AABB getDefaultAABB(){
		return new AABB(0.9f,0.9f,0.9f);
	}
	
	@Override
	public void update(){
		super.update();
		if (this.timeAlive > 4000f) {
			for (int i = 0; i < 16; ++i) {
				for (int j = 0; j < 16; ++j) {
					for (int k = 0; k < 16; ++k) {
						if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15) {
							float d0 = (float) i / 15.0F * 2.0F - 1.0F;
							float d1 = (float) j / 15.0F * 2.0F - 1.0F;
							float d2 = (float) k / 15.0F * 2.0F - 1.0F;
							float d3 = (float) Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
							d0 /= d3;
							d1 /= d3;
							d2 /= d3;
							float explosionStrength = 1.5f;
							float f = explosionStrength * (0.7F + Constants.rand.nextFloat() * 0.6F);
							float d4 = this.getX();
							float d6 = this.getY();
							float d8 = this.getZ();

							for (; f > 0.0F; f -= 0.4f) {
								Vector3f blockpos = new Vector3f(d4, d6, d8);
								worldManager.setTileAtPos(blockpos, Tile.Air.getId(), true);
								d4 += d0 * 0.30000001192092896D;
								d6 += d1 * 0.30000001192092896D;
								d8 += d2 * 0.30000001192092896D;
							}
						}
					}
				}
				this.setDead(true);
			}
		}
	}
}
