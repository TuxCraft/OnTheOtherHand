package mcdelta.ooh.handler;

import mcdelta.ooh.OOHData;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IPlayerTracker;

import static mcdelta.ooh.OOH.log;

public class PlayerTracker implements IPlayerTracker
{

	@Override
	public void onPlayerLogin (EntityPlayer player)
	{
		if(OOHData.getOOHData(player) == null)
		{
			log("Adding OOH data to " + player.username);
			OOHData.setOOHData(player, new OOHData(true, player.inventory.getStackInSlot(8)));
			
			log(player.inventory.getStackInSlot(8));
		}
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
