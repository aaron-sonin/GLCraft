package net.codepixl.GLCraft.world.item;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagByte;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagInteger;
import com.nishu.utils.Color4f;

import net.codepixl.GLCraft.render.RenderType;
import net.codepixl.GLCraft.render.Shape;
import net.codepixl.GLCraft.render.TextureManager;
import net.codepixl.GLCraft.render.util.Tesselator;
import net.codepixl.GLCraft.util.Spritesheet;
import net.codepixl.GLCraft.world.tile.Tile;

public class ItemStack{
	private Tile tile;
	private Item item;
	public int count;
	private boolean isTile;
	private boolean isNull;
	private byte meta;
	public ItemStack(Tile t){
		count = 1;
		isTile = true;
		tile = t;
		meta = 0;
	}
	public ItemStack(Tile t, byte meta){
		count = 1;
		isTile = true;
		tile = t;
		this.meta = meta;
	}
	public ItemStack(){
		isNull = true;
		isTile = false;
	}
	public ItemStack(Item i){
		count = 1;
		isTile = false;
		item = i;
		meta = 0;
	}
	public ItemStack(ItemStack s){
		this.tile = s.tile;
		this.item = s.item;
		this.count = s.count;
		this.isTile = s.isTile;
		this.isNull = s.isNull;
		this.meta = s.meta;
	}
	public ItemStack(ItemStack s, int count){
		this.tile = s.tile;
		this.item = s.item;
		this.count = count;
		this.isTile = s.isTile;
		this.meta = s.meta;
	}
	public ItemStack(Tile t, int count){
		this.count = count;
		isTile = true;
		tile = t;
		meta = 0;
	}
	public ItemStack(Tile t, int count, byte meta){
		this.count = count;
		isTile = true;
		tile = t;
		this.meta = meta;
	}
	public ItemStack(Item i, int count){
		this.count = count;
		isTile = false;
		item = i;
		meta = 0;
	}
	public boolean isTile(){
		return isTile;
	}
	public boolean isItem(){
		return !isTile;
	}
	public Tile getTile(){
		if(tile == null){
			return Tile.Void;
		}
		return tile;
	}
	public Item getItem(){
		if(item == null){
			return null;
		}
		return item;
	}
	public byte getId(){
		if(this.isItem()){
			return this.item.getId();
		}else{
			return this.tile.getId();
		}
	}
	public int addToStack(int count){
		int ret = count;
		for(int i = count; i > 0; i--){
			if(this.count >= 64){
				return ret;
			}
			this.count+=1;
			ret-=1;
		}
		return ret;
	}
	
	public int subFromStack(int count){
		int ret = count;
		for(int i = 0; i < count; i++){
			if(this.count <= 1){
				return ret;
			}
			this.count-=1;
			ret-=1;
		}
		return 0;
	}
	public boolean compatible(ItemStack itemstack) {
		if(this.isTile && itemstack.isTile() && this.tile == itemstack.tile && this.meta == itemstack.meta) return true;
		if(this.isItem() && itemstack.isItem() && this.item == itemstack.item && this.meta == itemstack.meta) return true;
		return false;
	}
	public boolean isNull() {
		return isNull;
	}
	public byte getMeta() {
		return meta;
	}
	public void setMeta(byte meta){
		this.meta = meta;
	}
	
	public float[] getTexCoords() {
		if(!this.isNull)
			if(this.isTile)
				if(this.tile.hasMetaTextures())
					return this.tile.getTexCoords(this.meta);
				else
					return this.tile.getTexCoords();
			else
				return this.item.getTexCoords();
		else
			return TextureManager.texture("misc.nothing");
	}
	
	public TagCompound toNBT(){
		TagCompound ret = new TagCompound("");
		ret.setTag(new TagByte("isItem",(byte) (this.isItem() ? 1 : 0 )));
		ret.setTag(new TagByte("id",this.getId()));
		ret.setTag(new TagByte("count",(byte)this.count));
		ret.setTag(new TagByte("meta",(byte)this.meta));
		return ret;
	}
	
	public TagCompound toNBT(String name){
		TagCompound ret = toNBT();
		ret.setName(name);
		return ret;
	}
	
	public static ItemStack fromNBT(TagCompound tag) throws UnexpectedTagTypeException, TagNotFoundException{
		ItemStack stack;
		if(tag.getByte("isItem") == 0){
			stack = new ItemStack(Tile.getTile(tag.getByte("id")));
		}else{
			stack = new ItemStack(Item.getItem(tag.getByte("id")));
		}
		stack.count = tag.getByte("count");
		try{
			stack.meta = tag.getByte("meta");
		}catch(TagNotFoundException e){} //In case the world was saved with an older version
			
		return stack;
	}
	
	public float[] getIconCoords() {
		if(!this.isNull)
			if(this.isTile)
				if(this.tile.hasMetaTextures())
					return this.tile.getIconCoords(this.meta);
				else
					return this.tile.getIconCoords();
			else
				return this.item.getTexCoords();
		else
			return TextureManager.texture("misc.nothing");
	}
	
	public void renderIcon(int x, int y, float size){
		if(!this.isNull() && this.count != 0){
			Spritesheet.atlas.bind();
			GL11.glPushMatrix();
			if(this.isTile() && (this.getTile().getRenderType() == RenderType.CUBE || (this.getTile().getRenderType() == RenderType.CUSTOM && this.getTile().getCustomRenderType() == RenderType.CUBE))){
				glTranslatef(x-size/2,y-size/2,0);
				glScalef(0.5f,0.5f,0.5f);
				GL11.glRotatef(140f,1.0f,0.0f,0.0f);
				GL11.glRotatef(45f,0.0f,1.0f,0.0f);
				glBegin(GL_QUADS);
				if(this.getTile().hasMetaTextures()){
					Shape.createCube(size/2.25f,-size*1.5f,0, new Color4f(1f,1f,1f,1f), this.getTile().getTexCoords(this.getMeta()), size);
				}else{
					Shape.createCube(size/2.25f,-size*1.5f,0, new Color4f(1f,1f,1f,1f), this.getTile().getTexCoords(), size);
				}
			}else{
				glTranslatef(x,y,0);
				glScalef(0.7f, 0.7f, 0.7f);
				glBegin(GL_QUADS);
				if(this.isItem())
					Shape.createCenteredSquare(0,0, new Color4f(1f,1f,1f,1f), this.getItem().getTexCoords(), size);
				else
					Shape.createCenteredSquare(0,0, new Color4f(1f,1f,1f,1f), this.getTile().getTexCoords(), size);
				
			}
			glEnd();
			GL11.glPopMatrix();
			if(this.count != 1)
				Tesselator.drawTextWithShadow(x, y, Integer.toString(this.count));
			TextureImpl.unbind();
		}
		TextureImpl.unbind();
	}
}
