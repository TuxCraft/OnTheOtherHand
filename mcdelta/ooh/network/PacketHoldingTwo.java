package mcdelta.ooh.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.Player;

public class PacketHoldingTwo extends PacketOOH
{
	public PacketHoldingTwo (EnumPacketTypes e)
	{
		super(e);
	}




	@Override
	public void writeData (DataOutputStream data) throws IOException
	{
		// data.writeUTF(key);
	}




	@Override
	public void readData (DataInputStream data) throws IOException
	{
		// key = data.readUTF();
	}




	@Override
	public void execute (INetworkManager manager, Player playerParam)
	{
		EntityPlayer player = (EntityPlayer) playerParam;
	}
}
