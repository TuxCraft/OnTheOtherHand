package mcdelta.ooh.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mcdelta.ooh.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.network.Player;

public class PacketUpdateOffhand extends PacketOOH
{
	private NBTTagCompound	data;




	public PacketUpdateOffhand ( )
	{
		super(EnumPacketTypes.UPDATE_OFFHAND);
	}




	public PacketUpdateOffhand (NBTTagCompound nbt)
	{
		super(EnumPacketTypes.UPDATE_OFFHAND);
		this.data = nbt;
	}




	@Override
	public void writeData (DataOutputStream dataStream) throws IOException
	{
		writeNBTTagCompound(data, dataStream);
	}




	@Override
	public void readData (DataInputStream dataStream) throws IOException
	{
		data = readNBTTagCompound(dataStream);
	}

	@Override
    public void execute(INetworkManager manager, Player playerParam)
    {
        EntityPlayer player = (EntityPlayer) playerParam;

        NBTHelper.setNBT(player, NBTHelper.OFFHAND_WEAPON, data);
    }
}
