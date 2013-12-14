package mcdelta.ooh.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

import mcdelta.ooh.OOHData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;

public class PacketSwingArm extends PacketOOH
{
	private int	entityID;




	public PacketSwingArm ( )
	{
		super(EnumPacketTypes.SWING_ARM);
	}




	public PacketSwingArm (EntityPlayer player)
	{
		super(EnumPacketTypes.SWING_ARM);
		this.entityID = player.entityId;
	}




	@Override
	public void writeData (DataOutputStream data) throws IOException
	{
		data.writeInt(entityID);
	}




	@Override
	public void readData (DataInputStream data) throws IOException
	{
		entityID = data.readInt();
	}




	@Override
	public void execute (INetworkManager manager, Player playerParam)
	{
		EntityPlayer player = (EntityPlayer) playerParam;
		EntityPlayer entity = (EntityPlayer) player.worldObj.getEntityByID(entityID);
		OOHData data = new OOHData();
		data.swingArm(entity);
	}
}
