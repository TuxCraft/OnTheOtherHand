package mcdelta.ooh;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class NBTHelper
{
	public static final String	OOH_COMPOUND_KEY	= "OOHData";
	public static final String	PREV_WEAPON_PROG	= "PrevWeaponProg";
	public static final String	DUAL_WIELD	     = "DualWield";
	public static final String	OFFHAND_WEAPON	 = "OffhandWeapon";
	public static final String	WEAPON_PROG	     = "WeaponProg";




	public static NBTTagCompound getOOHNBT (EntityPlayer player)
	{
		if (!player.getEntityData().hasKey(OOH_COMPOUND_KEY))
		{
			NBTTagCompound compound = new NBTTagCompound();
			compound.setBoolean(DUAL_WIELD, true);
			refresh(compound, player);
			setNBT(compound, player, PREV_WEAPON_PROG, 1.0F);
			setNBT(compound, player, WEAPON_PROG, 1.0F);
			player.getEntityData().setCompoundTag(OOH_COMPOUND_KEY, compound);
		}

		return player.getEntityData().getCompoundTag(OOH_COMPOUND_KEY);
	}




	public static void refresh (EntityPlayer player)
	{
		refresh(getOOHNBT(player), player);
	}




	public static void refresh (NBTTagCompound compound, EntityPlayer player)
	{
		ItemStack stack = player.inventory.getStackInSlot(0);
		compound.setCompoundTag(OFFHAND_WEAPON, stack == null ? new NBTTagCompound() : stack.writeToNBT(new NBTTagCompound()));

		Assets.p(stack + " " + compound);
	}




	public static ItemStack getOffhandItem (EntityPlayer player)
	{
		ItemStack stack = new ItemStack(0, 0, 0);
		NBTTagCompound compound = getOOHNBT(player).getCompoundTag(OFFHAND_WEAPON);

		if (compound.hasNoTags())
		{
			return null;
		}

		else
		{
			stack.readFromNBT(compound);
			return stack;
		}
	}




	public static boolean holdingTwo (EntityPlayer player)
	{
		return getOOHNBT(player).getBoolean(DUAL_WIELD);
	}




	public static void setHoldingTwo (EntityPlayer player, boolean bool)
	{
		getOOHNBT(player).setBoolean(DUAL_WIELD, bool);
	}




	public static void setNBT (EntityPlayer player, String s, float f)
	{
		setNBT(getOOHNBT(player), player, s, f);
	}




	private static void setNBT (NBTTagCompound compound, EntityPlayer player, String s, float f)
	{
		compound.setFloat(s, f);
	}




	public static float getNBT (EntityPlayer player, String s)
	{
		return getOOHNBT(player).getFloat(s);
	}
}
