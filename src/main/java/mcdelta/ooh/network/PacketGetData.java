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

public class PacketGetData extends PacketOOH
{

	private int	entityInQuestion;
	private int	playerID;




	public PacketGetData ( )
	{
		super(EnumPacketTypes.OOH_DATA_GET);
	}




	public PacketGetData (Entity entity, Entity entity2)
	{
		this(entity.entityId, entity2.entityId);
	}




	public PacketGetData (int i1, int i2)
	{
		super(EnumPacketTypes.OOH_DATA_GET);

		entityInQuestion = i1;
		playerID = i2;
	}




	@Override
	public void writeData (DataOutputStream data) throws IOException
	{
		data.writeInt(entityInQuestion);
		data.writeInt(playerID);
	}




	@Override
	public void readData (DataInputStream data) throws IOException
	{
		entityInQuestion = data.readInt();
		playerID = data.readInt();
	}




	@Override
	public void execute (INetworkManager manager, Player playerParam)
	{
		EntityPlayer player = (EntityPlayer) playerParam;
		EntityPlayer thePlayer = (EntityPlayer) player.worldObj.getEntityByID(playerID);
		Entity entity = player.worldObj.getEntityByID(entityInQuestion);

		if (isServer())
		{
			PacketDispatcher.sendPacketToPlayer(EnumPacketTypes.populatePacket(new PacketSetData(entity, OOHData.getOOHData(entity))), (Player) thePlayer);
		}
	}

}
