package net.codepixl.GLCraft.world.entity.tileentity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.evilco.mc.nbt.error.TagNotFoundException;
import com.evilco.mc.nbt.error.UnexpectedTagTypeException;
import com.evilco.mc.nbt.tag.TagByte;
import com.evilco.mc.nbt.tag.TagCompound;
import com.evilco.mc.nbt.tag.TagInteger;
import com.evilco.mc.nbt.tag.TagList;

import net.codepixl.GLCraft.util.Constants;
import net.codepixl.GLCraft.world.WorldManager;
import net.codepixl.GLCraft.world.entity.Entity;
import net.codepixl.GLCraft.world.entity.EntityItem;
import net.codepixl.GLCraft.world.entity.NBTUtil;
import net.codepixl.GLCraft.world.item.Item;
import net.codepixl.GLCraft.world.item.ItemStack;
import net.codepixl.GLCraft.world.tile.Tile;

public class TileEntityContainer extends TileEntity{
	
	private ItemStack[] inventory;
	
	public TileEntityContainer(int x, int y, int z, int size, WorldManager worldManager) {
		super(x, y, z, worldManager);
		setInventory(new ItemStack[size]);
		for(int i = 0; i < getInventory().length; i++){
			getInventory()[i] = new ItemStack();
		}
	}
	
	public TileEntityContainer(int x, int y, int z, ItemStack[] inventory, WorldManager worldManager){
		super(x,y,z,worldManager);
		this.setInventory(inventory);
	}
	
	public ItemStack getSlot(int slot){
		return getInventory()[slot];
	}
	
	public ItemStack[] getInventory(){
		return inventory;
	}
	
	public int addToContainer(ItemStack s){
		int c = s.count;
		ArrayList<Integer> blankSlots = new ArrayList<Integer>();
		ArrayList<Integer> compatibleSlots = new ArrayList<Integer>();
		for(int i = 0; i < getInventory().length; i++){
			if(getInventory()[i].isNull()){
				blankSlots.add(i);
			}else if(getInventory()[i].compatible(s)){
				compatibleSlots.add(i);
			}
		}
		
		for(int i = 0; i < compatibleSlots.size(); i++){
			int cr = getInventory()[compatibleSlots.get(i)].addToStack(c);
			c=cr;
			if(cr == 0){
				return 0;
			}
		}
		
		if(blankSlots.size() > 0){
			getInventory()[blankSlots.get(0)] = new ItemStack(s);
			return 0;
		}
		return c;
	}
	
	public void dropAllItems(){
		for(int i = 0; i < getInventory().length; i++){
			if(!getInventory()[i].isNull()){
				Vector3f pos = new Vector3f(this.pos.x+Constants.randFloat(0, 1), this.pos.y+Constants.randFloat(0, 1), this.pos.z+Constants.randFloat(0, 1));
				worldManager.spawnEntity(new EntityItem(getInventory()[i],pos,worldManager));
				getInventory()[i] = new ItemStack();
			}
		}
	}
	
	public void writeToNBT(TagCompound t){
		TagList inventory = new TagList("Inventory");
		for(int i = 0; i < getInventory().length; i++){
			ItemStack stack = getInventory()[i];
			if(!stack.isNull()){
				TagCompound slot = stack.toNBT();
				slot.setTag(new TagInteger("slot",i));
				inventory.addTag(slot);
			}
		}
		t.setTag (inventory);
		TagInteger size = new TagInteger("size",getInventory().length);
		t.setTag(size);
	}
	
	public static Entity fromNBT(TagCompound t, WorldManager w) throws UnexpectedTagTypeException, TagNotFoundException {
		Vector3f pos = NBTUtil.vecFromList("Pos",t);
		if(t.getList("Inventory", TagCompound.class) != null){
			List<TagCompound> inventory = t.getList("Inventory",TagCompound.class);
			ItemStack[] is = new ItemStack[t.getInteger("size")];
			for(int i = 0; i < t.getInteger("size"); i++)
				is[i] = new ItemStack();
			Iterator<TagCompound> i = inventory.iterator();
			while(i.hasNext()){
				TagCompound t2 = i.next();
				int slot = t2.getInteger("slot");
				ItemStack stack = ItemStack.fromNBT(t2);
				is[slot] = stack;
			}
			return new TileEntityContainer((int)pos.x, (int)pos.y, (int)pos.z, is, w);
		}
		
		return null;
	}

	public void setInventory(ItemStack[] inventory) {
		this.inventory = inventory;
	}

}
