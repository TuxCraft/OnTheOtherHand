package mcdelta.ooh.handler;

import static mcdelta.ooh.OOH.idMetaDamageMatch;
import static mcdelta.ooh.OOH.isClient;
import static mcdelta.ooh.OOH.isServer;
import static mcdelta.ooh.OOH.log;

import java.util.EnumSet;

import mcdelta.ooh.OOHData;
import mcdelta.ooh.network.EnumPacketTypes;
import mcdelta.ooh.network.PacketGetData;
import mcdelta.ooh.network.PacketSetData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class PlayerTickHandler implements ITickHandler
{
	private KeyBinding	leftClick;
	private KeyBinding	rightClick;




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
			OOHData data = OOHData.getOOHData(player);

			if (data != null)
			{
				if (data.doubleEngaged)
				{
					if (isServer())
					{
						if (!idMetaDamageMatch(data.secondItem, player.inventory.getStackInSlot(8)))
						{
							data.secondItem = player.inventory.getStackInSlot(8);
							OOHData.setOOHData(player, data);
							PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketSetData(player, OOHData.getOOHData(player))));
						}
					}

					if (isClient())
					{
						data.swingProgress[1] = data.swingProgress[0];

						if (leftClick == null)
						{
							GameSettings settings = Minecraft.getMinecraft().gameSettings;

							leftClick = settings.keyBindAttack;
							rightClick = settings.keyBindUseItem;
						}

						if (rightClick.pressed)
						{
							if (!data.swinging || data.swingProgressInt >= getArmSwingAnimationEnd(player) / 2 || data.swingProgressInt < 0)
							{
								data.swingProgressInt = -1;
								data.swinging = true;
							}
						}

						updateArmSwing(player, data);
						OOHData.setOOHData(player, data);

						if (data.swinging)
						{
							//PacketDispatcher.sendPacketToServer(EnumPacketTypes.populatePacket(new PacketSetData(player, OOHData.getOOHData(player), true)));
						}
					}
				}
			}

			else
			{
				PacketDispatcher.sendPacketToServer(EnumPacketTypes.populatePacket(new PacketGetData(player, Minecraft.getMinecraft().thePlayer)));
			}
		}
	}




	private void updateArmSwing (EntityPlayer player, OOHData data)
	{
		int i = getArmSwingAnimationEnd(player);

		if (data.swinging)
		{
			++data.swingProgressInt;

			if (data.swingProgressInt >= i)
			{
				data.swingProgressInt = 0;
				data.swinging = false;
			}
		}
		else
		{
			data.swingProgressInt = 0;
		}

		data.swingProgress[0] = (float) data.swingProgressInt / (float) i;

		// log(data.swingProgress[0]);
	}




	private int getArmSwingAnimationEnd (EntityPlayer player)
	{
		return player.isPotionActive(Potion.digSpeed) ? 6 - (1 + player.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1 : (player.isPotionActive(Potion.digSlowdown) ? 6 + (1 + player.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
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
