package mcdelta.ooh;

import java.util.EnumSet;

import mcdelta.ooh.network.EnumPacketTypes;
import mcdelta.ooh.network.PacketUpdateOffhand;
import mcdelta.ooh.network.PacketUpdateTwoItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class PlayerTickHandler implements ITickHandler
{

	@Override
	public void tickStart (EnumSet<TickType> type, Object... tickData)
	{

	}




	@Override
	public void tickEnd (EnumSet<TickType> type, Object... tickData)
	{
		if (type.contains(TickType.PLAYER))
		{
			EntityPlayer player = (EntityPlayer) tickData[0];

			if (DualWield.checkForChange(player))
			{
				NBTHelper.refresh(player);

				if (Assets.isServer())
				{
					NBTTagCompound tag = new NBTTagCompound();
					NBTHelper.getOffhandItem(player).writeToNBT(tag);
					PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketUpdateOffhand(tag)));
				}
			}

			if (NBTHelper.holdingTwo(player) && player.inventory.currentItem != 8)
			{
				NBTHelper.setHoldingTwo(player, false);
				PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketUpdateTwoItems(false)));
			}
		}
	}




	@Override
	public EnumSet<TickType> ticks ()
	{
		return EnumSet.of(TickType.PLAYER);
	}




	@Override
	public String getLabel ()
	{
		return "OOHTickTock";
	}

}
