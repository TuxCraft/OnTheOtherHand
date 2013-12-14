package mcdelta.ooh.handler;

import static mcdelta.ooh.OOH.getArmSwingAnimationEnd;
import static mcdelta.ooh.OOH.idMetaDamageMatch;
import static mcdelta.ooh.OOH.isClient;
import static mcdelta.ooh.OOH.isServer;

import java.util.EnumSet;

import mcdelta.ooh.OOHData;
import mcdelta.ooh.network.EnumPacketTypes;
import mcdelta.ooh.network.PacketSetData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class PlayerTickHandler implements ITickHandler
{
	private KeyBinding	leftClick;
	private KeyBinding	rightClick;
	private int	       rightHeldTime	= 0;




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

						if (rightHeldTime == 1 && player.username == Minecraft.getMinecraft().thePlayer.username)
						{
							if (!data.swinging || data.swingProgressInt >= getArmSwingAnimationEnd(player) / 2 || data.swingProgressInt < 0)
							{
								data.startSwing = true;
								OOHData.setOOHData(player, data);
								PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketSetData(player, OOHData.getOOHData(player))));
							}
						}

						if (data.startSwing && rightHeldTime == 1)
						{
							data.swingArm(player);

							data.startSwing = false;
							OOHData.setOOHData(player, data);
							PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketSetData(player, OOHData.getOOHData(player))));
						}

						if (rightClick.pressed)
						{
							rightHeldTime++;
						}
						else
						{
							rightHeldTime = 0;
						}

						updateArmSwing(player, data);
						OOHData.setOOHData(player, data);
					}
				}
			}

			else
			{
				// PacketDispatcher.sendPacketToServer(EnumPacketTypes.populatePacket(new
				// PacketGetData(player, Minecraft.getMinecraft().thePlayer)));
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
