package net.codepixl.GLCraft.item;

import java.util.HashMap;

import com.nishu.utils.Color4f;

public class Item {
	
	public static HashMap<Byte, Item> itemMap = new HashMap<Byte, Item>();
	public static Item seeds = new ItemSeeds();
	
	public String getName(){
		return "Un-named item";
	}
	
	public byte getId(){
		return -1;
	}
	
	public static Item getItem(byte id){
		return itemMap.get(id);
	}
	
	public Color4f getColor() {
		return new Color4f(1,1,1,1);
	}

	public float[] getTexCoords() {
		return new float[]{0,0};
	}
	
	public void registerItem() {
		System.out.println("Registering Item "+getName()+" ("+getId()+")");
		Item.itemMap.put(getId(), this);
	}
	
	public Item(){
		if(this.getClass() != Item.class){
			registerItem();
		}
	}

}