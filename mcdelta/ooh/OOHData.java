package mcdelta.ooh;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static mcdelta.ooh.OOH.*;

public class OOHData
{
	public float[]	 swingProgress	= new float[]
	                               { 0, 0 };
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
		compound.setFloat("swingProgressA", swingProgress[0]);
		compound.setFloat("swingProgressB", swingProgress[1]);

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

		swingProgress[0] = compound.getFloat("swingProgressA");
		swingProgress[1] = compound.getFloat("swingProgressB");

		return this;
	}




	public static OOHData getOOHData (Entity player)
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




	public static void setOOHData (Entity player, OOHData data)
	{
		player.getEntityData().setCompoundTag("OOHData", data.writeToNBT(new NBTTagCompound()));
	}




	@Override
	public String toString ()
	{
		return "OOHData[ doubleEngaged:" + doubleEngaged + ", secondItem:" + secondItem + "]";
	}




	public float getSwingProgress (float f)
	{
		log(swingProgress[0]);
		return swingProgress[0];
	}
}
