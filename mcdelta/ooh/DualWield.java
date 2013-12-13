package mcdelta.ooh;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class DualWield
{
	public static boolean checkForChange (EntityPlayer player)
	{
		ItemStack stack = NBTHelper.getOffhandItem(player);
		ItemStack stack2 = player.inventory.getStackInSlot(0);

		if (stack != stack2)
		{
			if (stack == null || stack2 == null)
			{
				return true;
			}

			if (stack.itemID != stack2.itemID)
			{
				return true;
			}
		}

		return false;
	}
}
