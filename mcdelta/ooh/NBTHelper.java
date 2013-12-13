package mcdelta.ooh;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class NBTHelper
{
	public static final String	OOH_COMPOUND_KEY	= "OOHData";
	public static final String	DUAL_WIELD	     = "DualWield";




	public static NBTTagCompound getPlayerNBT (EntityPlayer player)
	{
		if (!player.getEntityData().hasKey(OOH_COMPOUND_KEY))
		{
			player.getEntityData().setCompoundTag(OOH_COMPOUND_KEY, new NBTTagCompound());
		}

		return player.getEntityData().getCompoundTag(OOH_COMPOUND_KEY);
	}
}
