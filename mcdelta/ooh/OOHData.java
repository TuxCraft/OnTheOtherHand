package mcdelta.ooh;

import static mcdelta.ooh.OOH.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class OOHData
{
	public float[]	 swingProgress;
	public float[]	 equipProgress;
	public int	     swingProgressInt;
	public boolean	 swinging;
	public boolean	 doubleEngaged;
	public boolean	 startSwing;
	public ItemStack	secondItem;
	public int	     maxUseDuration;




	public OOHData ( )
	{
		swinging = false;
		swingProgressInt = 0;
		swingProgress = new float[]
		{ 0, 0 };
		startSwing = false;
		equipProgress = new float[]
		{ 0, 0 };
		maxUseDuration = 0;
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
		compound.setFloat("swingProgressA", swingProgress[0]);
		compound.setFloat("swingProgressB", swingProgress[1]);
		compound.setBoolean("swinging", swinging);
		compound.setInteger("swingProgressInt", swingProgressInt);
		compound.setBoolean("startSwing", startSwing);
		compound.setFloat("equipProgressA", equipProgress[0]);
		compound.setFloat("equipProgressB", equipProgress[1]);
		compound.setInteger("maxUseDuration", maxUseDuration);

		return compound;
	}




	public OOHData readFromNBT (NBTTagCompound compound)
	{
		doubleEngaged = compound.getBoolean("doubleEngaged");
		swingProgress[0] = compound.getFloat("swingProgressA");
		swingProgress[1] = compound.getFloat("swingProgressB");
		swinging = compound.getBoolean("swinging");
		swingProgressInt = compound.getInteger("swingProgressInt");
		startSwing = compound.getBoolean("startSwing");
		equipProgress[0] = compound.getFloat("equipProgressA");
		equipProgress[1] = compound.getFloat("equipProgressB");
		maxUseDuration = compound.getInteger("maxUseDuration");

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
		if (player == null)
		{
			return;
		}

		player.getEntityData().setCompoundTag("OOHData", data.writeToNBT(new NBTTagCompound()));
	}




	@Override
	public String toString ()
	{
		return "OOHData[ doubleEngaged:" + doubleEngaged + ", secondItem:" + secondItem + "]";
	}




	public float getSwingProgress (float f)
	{
		float f1 = this.swingProgress[0] - this.swingProgress[1];

		if (f1 < 0.0F)
		{
			++f1;
		}

		return this.swingProgress[1] + f1 * f;
	}




	public void swingArm (EntityPlayer player)
	{
		ItemStack stack = this.secondItem;

		if (stack != null && stack.getItem() != null)
		{
			Item item = stack.getItem();
			if (item.onEntitySwing(player, stack))
			{
				return;
			}
		}

		if (!this.swinging || this.swingProgressInt >= getArmSwingAnimationEnd(player) / 2 || this.swingProgressInt < 0)
		{
			this.swingProgressInt = -1;
			this.swinging = true;
		}
	}




	public void updateArmSwing (EntityPlayer player)
	{
		int i = getArmSwingAnimationEnd(player);

		if (this.swinging)
		{
			++this.swingProgressInt;

			if (this.swingProgressInt >= i)
			{
				this.swingProgressInt = 0;
				this.swinging = false;
			}
		}
		else
		{
			this.swingProgressInt = 0;
		}

		this.swingProgress[0] = (float) this.swingProgressInt / (float) i;
	}




	public void resetEquippedProgress ()
	{
		equipProgress[0] = 0.0F;
	}
}
