package mcdelta.ooh.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mcdelta.ooh.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.network.Player;

public class PacketUpdateTwoItems extends PacketOOH
{
	private boolean	data;




	public PacketUpdateTwoItems ( )
	{
		super(EnumPacketTypes.UPDATE_TWOITEMS);
	}




	public PacketUpdateTwoItems (boolean b)
	{
		super(EnumPacketTypes.UPDATE_TWOITEMS);
		this.data = b;
	}




	@Override
	public void writeData (DataOutputStream dataStream) throws IOException
	{
		dataStream.writeBoolean(data);
	}




	@Override
	public void readData (DataInputStream dataStream) throws IOException
	{
		data = dataStream.readBoolean();
	}




	@Override
	public void execute (INetworkManager manager, Player playerParam)
	{
		EntityPlayer player = (EntityPlayer) playerParam;

		NBTHelper.setHoldingTwo(player, data);
	}
}
