package net.codepixl.GLCraft.network.packet;

import org.lwjgl.util.vector.Vector3f;

public class PacketPlayerAdd extends Packet {

	private static final long serialVersionUID = 3259174108375172081L;
	
	public int entityID;
	public String name;
	public float x,y,z;
	
	public PacketPlayerAdd(int id, String name, Vector3f pos){
		this.entityID = id;
		this.name = name;
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;
	}

}
