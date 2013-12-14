package mcdelta.ooh.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mcdelta.ooh.OOHData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

import static mcdelta.ooh.OOH.*;

public class PacketSetData extends PacketOOH
{

	private int	           entityID;
	private NBTTagCompound	tagCompound;
	private boolean	       sendOutData;




	public PacketSetData ( )
	{
		super(EnumPacketTypes.OOH_DATA_SET);
	}




	public PacketSetData (Entity entity, OOHData data)
	{
		this(entity.entityId, data.writeToNBT(new NBTTagCompound()), false);
	}




	public PacketSetData (Entity entity, OOHData data, boolean b)
	{
		this(entity.entityId, data.writeToNBT(new NBTTagCompound()), b);
	}




	public PacketSetData (int id, OOHData data)
	{
		this(id, data.writeToNBT(new NBTTagCompound()), false);
	}




	public PacketSetData (int id, NBTTagCompound data, boolean b)
	{
		super(EnumPacketTypes.OOH_DATA_SET);

		entityID = id;
		tagCompound = data;
		sendOutData = b;
	}




	@Override
	public void writeData (DataOutputStream data) throws IOException
	{
		data.writeInt(entityID);
		writeNBTTagCompound(tagCompound, data);
		data.writeBoolean(sendOutData);
	}




	@Override
	public void readData (DataInputStream data) throws IOException
	{
		entityID = data.readInt();
		tagCompound = readNBTTagCompound(data);
		sendOutData = data.readBoolean();
	}




	@Override
	public void execute (INetworkManager manager, Player playerParam)
	{
		EntityPlayer player = (EntityPlayer) playerParam;
		Entity entity = player.worldObj.getEntityByID(entityID);
		OOHData data = new OOHData();
		OOHData.setOOHData(entity, data.readFromNBT(tagCompound));

		if (sendOutData)
		{
			PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketSetData(entityID, tagCompound, false)));
		}
	}

}
