package mcdelta.ooh.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mcdelta.ooh.OOHData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.network.Player;

import static mcdelta.ooh.OOH.*;

public class PacketOOHData extends PacketOOH
{

	private int	           entityID;
	private NBTTagCompound	tagCompound;




	public PacketOOHData ( )
	{
		super(EnumPacketTypes.UPDATE_OOHDATA);
	}

	public PacketOOHData (Entity entity, OOHData data)
	{
		this(entity.entityId, data);
	}


	public PacketOOHData (int id, OOHData data)
	{
		super(EnumPacketTypes.UPDATE_OOHDATA);

		entityID = id;
		tagCompound = data.writeToNBT(new NBTTagCompound());
	}




	@Override
	public void writeData (DataOutputStream data) throws IOException
	{
		data.writeInt(entityID);
		writeNBTTagCompound(tagCompound, data);
	}




	@Override
	public void readData (DataInputStream data) throws IOException
	{
		entityID = data.readInt();
		tagCompound = readNBTTagCompound(data);
	}




	@Override
	public void execute (INetworkManager manager, Player playerParam)
	{
		EntityPlayer player = (EntityPlayer) playerParam;
		Entity entity = player.worldObj.getEntityByID(entityID);
		OOHData data = new OOHData();
		OOHData.setOOHData(entity, data.readFromNBT(tagCompound));
	}

}