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
		if (OOHData.getOOHData(player) == null)
		{
			log("Adding OOH data to " + player.username);
			OOHData.setOOHData(player, new OOHData(true, player.inventory.getStackInSlot(8)));
		}

		PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketSetData(player, OOHData.getOOHData(player))));
	}




	@Override
	public void onPlayerLogout (EntityPlayer player)
	{
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
