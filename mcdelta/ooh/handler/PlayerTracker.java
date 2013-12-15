package mcdelta.ooh.handler;

import static mcdelta.ooh.OOH.log;
import mcdelta.ooh.OOHData;
import mcdelta.ooh.network.EnumPacketTypes;
import mcdelta.ooh.network.PacketSetData;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.PacketDispatcher;

public class PlayerTracker implements IPlayerTracker
{

	@Override
	public void onPlayerLogin (EntityPlayer player)
	{
		OOHData data = OOHData.getOOHData(player);
		
		if (data == null)
		{
			log("Adding OOH data to " + player.username);
			int slot = (player.inventory.currentItem - 1 < 0) ? 8 : player.inventory.currentItem - 1;
			data = new OOHData(true, player.inventory.getStackInSlot(slot));
			OOHData.setOOHData(player, data);
		}
		
		else
		{
			data.doubleEngaged = false;
			data.swinging = false;
			data.swingProgressInt = 0;
			data.swingProgress = new float[]
			{ 0, 0 };
			data.startSwing = false;
			data.equipProgress = new float[]
			{ 0, 0 };
		}

		OOHData.setOOHData(player, data);
		PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketSetData(player, OOHData.getOOHData(player))));
	}




	@Override
	public void onPlayerLogout (EntityPlayer player)
	{
		OOHData data = OOHData.getOOHData(player);
		data.doubleEngaged = false;
		
		PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketSetData(player, data)));
	}




	@Override
	public void onPlayerChangedDimension (EntityPlayer player)
	{
	}




	@Override
	public void onPlayerRespawn (EntityPlayer player)
	{
	}
}
