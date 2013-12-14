package mcdelta.ooh;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class OOHData
{
	public boolean	 doubleEngaged;
	public ItemStack	secondItem;




	public OOHData ( )
	{

	}




	public OOHData (boolean b, ItemStack item)
	{
		doubleEngaged = b;
		secondItem = item;
	}




	public NBTTagCompound writeToNBT (NBTTagCompound compound)
	{
		compound.setBoolean("doubleEngaged", doubleEngaged);
		compound.setCompoundTag("secondItem", secondItem == null ? new NBTTagCompound() : secondItem.writeToNBT(new NBTTagCompound()));

		return compound;
	}




	public OOHData readFromNBT (NBTTagCompound compound)
	{
		doubleEngaged = compound.getBoolean("doubleEngaged");

		if (compound.getCompoundTag("secondItem").hasNoTags())
		{
			secondItem = null;
		}
		else
		{
			secondItem = new ItemStack(0, 0, 0);
			secondItem.readFromNBT(compound.getCompoundTag("secondItem"));
		}

		return this;
	}




	public static OOHData getOOHData (EntityPlayer player)
	{
		if (player.getEntityData().hasKey("OOHData"))
		{
			OOHData data = new OOHData();
			return data.readFromNBT(player.getEntityData().getCompoundTag("OOHData"));
		}

		else
		{
			return null;
		}
	}




	public static void setOOHData (EntityPlayer player, OOHData data)
	{
		player.getEntityData().setCompoundTag("OOHData", data.writeToNBT(new NBTTagCompound()));
	}
}
