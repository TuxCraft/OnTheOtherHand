package mcdelta.ooh.handler;

import static mcdelta.ooh.OOH.*;

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
	private KeyBinding	key;
	private int	       rightHeldTime	= 0;
	private int	       leftHeldTime	 = 0;
	private int	       cooldown	     = 0;




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

							key = new KeyBinding("nope", 0);
						}

						if (Minecraft.getMinecraft().thePlayer.username.equals(player.username))
						{
							if (cooldown != 0)
							{
								cooldown--;
							}

							if (rightClick.pressed)
							{
								rightHeldTime++;
							}
							else
							{
								rightHeldTime = 0;
							}
							
							if (leftClick.pressed)
							{
								leftHeldTime++;
							}
							else
							{
								leftHeldTime = 0;
							}

							if (rightHeldTime == 1 && cooldown == 0)
							{
								cooldown = 4;

								data.startSwing = true;
								PacketDispatcher.sendPacketToServer(EnumPacketTypes.populatePacket(new PacketSetData(player, data, true)));
							}
							
							if (leftHeldTime == 1)
							{
								player.swingItem();
							}

							GameSettings settings = Minecraft.getMinecraft().gameSettings;

							settings.keyBindAttack = key;
							settings.keyBindUseItem = key;
						}

						if (data.startSwing)
						{
							data.swingArm(player);
							data.startSwing = false;
						}

						updateArmSwing(player, data);
						OOHData.setOOHData(player, data);
					}
				}

				else
				{
					if (isClient())
					{
						GameSettings settings = Minecraft.getMinecraft().gameSettings;

						settings.keyBindAttack = leftClick;
						settings.keyBindUseItem = rightClick;
					}
				}
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
