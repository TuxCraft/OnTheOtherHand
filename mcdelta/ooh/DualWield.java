package mcdelta.ooh;

import net.minecraft.entity.player.EntityPlayer;

public class DualWield
{
	public static boolean checkForChange(EntityPlayer player)
	{
		if(NBTHelper.getOffhandItem(player) != player.inventory.getStackInSlot(8))
		{
			return true;
		}
		
		return false;
	}
}
