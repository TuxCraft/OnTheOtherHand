package mcdelta.ooh.network;

import static mcdelta.ooh.OOH.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mcdelta.ooh.OOHData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.util.ChatMessageComponent;
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




	public PacketSwingArm (Entity player, boolean b)
	{
		super(EnumPacketTypes.SWING_ARM);
		this.entityID = player.entityId;
		this.sendOutData = b;
	}




	@Override
	public void writeData (DataOutputStream data) throws IOException
	{
		data.writeInt(entityID);
		data.writeBoolean(sendOutData);
	}




	@Override
	public void readData (DataInputStream data) throws IOException
	{
		entityID = data.readInt();
		sendOutData = data.readBoolean();
	}




	@Override
	public void execute (INetworkManager manager, Player playerParam)
	{
		EntityPlayer player = (EntityPlayer) playerParam;
		EntityPlayer thePlayer = (EntityPlayer) player.worldObj.getEntityByID(entityID);
		OOHData data = new OOHData();
		data.swingArm(thePlayer);

		//player.sendChatToPlayer(ChatMessageComponent.createFromText(String.valueOf(thePlayer.username + " " + isClient())));

		if (sendOutData)
		{
			PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketSwingArm(thePlayer, false)));
		}
	}
}
