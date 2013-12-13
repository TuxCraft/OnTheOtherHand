package mcdelta.ooh;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IPlayerTracker;

public class PlayerTracker implements IPlayerTracker
{
	@Override
	public void onPlayerLogin (EntityPlayer player)
	{
		NBTHelper.refresh(player);
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
