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

public class PacketStartSwing extends PacketOOH
{
	private int	entityID;




	public PacketStartSwing ( )
	{
		super(EnumPacketTypes.START_SWING);
	}




	public PacketStartSwing (Entity entity)
	{
		super(EnumPacketTypes.START_SWING);
		this.entityID = entity.entityId;
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
		Entity entity = player.worldObj.getEntityByID(entityID);
		OOHData data = OOHData.getOOHData(entity);
		data.startSwing = true;
		OOHData.setOOHData(player, data);
	}
}
