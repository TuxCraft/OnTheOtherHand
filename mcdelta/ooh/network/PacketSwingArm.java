package mcdelta.ooh.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mcdelta.ooh.OOHData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketSwingArm extends PacketOOH
{
	private int	    entityID;
	private boolean	sendOutData;




	public PacketSwingArm ( )
	{
		super(EnumPacketTypes.SWING_ARM);
	}




	public PacketSwingArm (EntityPlayer player)
	{
		this(player.entityId);
	}




	public PacketSwingArm (EntityPlayer player, boolean b)
	{
		this(player.entityId);
		this.sendOutData = b;
	}




	public PacketSwingArm (int i, boolean b)
	{
		this(i);
		this.sendOutData = b;
	}




	public PacketSwingArm (int i)
	{
		super(EnumPacketTypes.SWING_ARM);
		this.entityID = i;
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

		if (sendOutData)
		{
			PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketSwingArm(entityID, false)));
		}
	}
}
