package mcdelta.ooh;

import java.util.EnumSet;

import mcdelta.ooh.network.EnumPacketTypes;
import mcdelta.ooh.network.PacketUpdateOffhand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

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
				NBTTagCompound tag = new NBTTagCompound();
				NBTHelper.getOffhandItem(player).writeToNBT(tag);
				((WorldServer) player.worldObj).getEntityTracker().sendPacketToAllPlayersTrackingEntity(player, EnumPacketTypes.populatePacket(new PacketUpdateOffhand(tag)));
			}

			if (NBTHelper.holdingTwo(player) && player.inventory.currentItem != 8)
			{
				NBTHelper.setHoldingTwo(player, false);
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
