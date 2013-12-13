package mcdelta.ooh;

import net.minecraft.nbt.NBTTagCompound;

public class OOHData
{
	public boolean	doubleEngaged;
	public




	public Data ( )
	{
		doubleEngaged = true;
	}




	public void writeToNBT (NBTTagCompound compound)
	{
		compound.setBoolean("doubleEngaged", doubleEngaged);
	}




	public void readFromNBT (NBTTagCompound compound)
	{
		doubleEngaged = compound.getBoolean("doubleEngaged");
	}
}
