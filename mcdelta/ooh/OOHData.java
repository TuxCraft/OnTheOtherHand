package mcdelta.ooh;

import static mcdelta.ooh.OOH.getArmSwingAnimationEnd;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class OOHData
{
	@SideOnly (Side.CLIENT)
	public float[]	 swingProgress;

	@SideOnly (Side.CLIENT)
	public int	     swingProgressInt;

	@SideOnly (Side.CLIENT)
	public boolean	 swinging;

	public boolean	 doubleEngaged;
	public ItemStack	secondItem;




	@SideOnly (Side.CLIENT)
	public OOHData ( )
	{
		swinging = false;
		swingProgressInt = 0;
		swingProgress = new float[]
		{ 0, 0 };
	}




	public OOHData (boolean b, ItemStack item)
	{
		this();
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




	@SideOnly (Side.CLIENT)
	public float getSwingProgress (float f)
	{
		float f1 = this.swingProgress[0] - this.swingProgress[1];

		if (f1 < 0.0F)
		{
			++f1;
		}

		return this.swingProgress[1] + f1 * f;
	}




	@SideOnly (Side.CLIENT)
	public void swingArm (EntityPlayer player)
	{
		if (!this.swinging || this.swingProgressInt >= getArmSwingAnimationEnd(player) / 2 || this.swingProgressInt < 0)
		{
			this.swingProgressInt = -1;
			this.swinging = true;
		}
	}
}
