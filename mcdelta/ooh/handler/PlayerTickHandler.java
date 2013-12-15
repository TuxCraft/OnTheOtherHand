package mcdelta.ooh.handler;

import static mcdelta.ooh.OOH.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumSet;

import mcdelta.ooh.OOHData;
import mcdelta.ooh.network.EnumPacketTypes;
import mcdelta.ooh.network.PacketSetData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
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
	private int	       cooldownRight	= 0;
	private int	       cooldownLeft	 = 0;
	private boolean	   repeat	     = false;




	@Override
	public void tickStart (EnumSet<TickType> type, Object... tickData)
	{
		if (type.contains(TickType.PLAYER))
		{
			EntityPlayer player = (EntityPlayer) tickData[0];
			OOHData data = OOHData.getOOHData(player);

			if (data != null)
			{
				if (data.doubleEngaged)
				{
					if (isClient())
					{
						GameSettings settings = Minecraft.getMinecraft().gameSettings;

						if (leftClick == null || rightClick == null || key == null)
						{
							rightClick = settings.keyBindAttack;
							leftClick = settings.keyBindUseItem;

							key = new KeyBinding("nope", 70);
						}

						settings.keyBindAttack = key;
						settings.keyBindUseItem = key;
					}
				}
			}
		}
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
					if (isClient())
					{
						GameSettings settings = Minecraft.getMinecraft().gameSettings;
						data.swingProgress[1] = data.swingProgress[0];

						if (Minecraft.getMinecraft().thePlayer.username.equals(player.username))
						{
							
						}

						if (data.startSwing)
						{
							data.startSwing = false;
							data.swingArm(player);
						}

						data.updateArmSwing(player);
					}
				}

				else
				{
					OOHData.setOOHData(player, new OOHData(false, player.getCurrentEquippedItem()));

					if (isClient())
					{
						if (Minecraft.getMinecraft().thePlayer.username.equals(player.username))
						{
							GameSettings settings = Minecraft.getMinecraft().gameSettings;

							if (leftClick != null && rightClick != null)
							{
								settings.keyBindAttack = rightClick;
								settings.keyBindUseItem = leftClick;
							}
						}
					}
				}
			}
		}
	}




	private boolean click (EntityPlayer player, int click, boolean offHand)
	{
		ItemStack stack = player.getCurrentEquippedItem();
		MovingObjectPosition target = Minecraft.getMinecraft().objectMouseOver;
		OOHData data = OOHData.getOOHData(player);

		if (target == null)
		{
			if (click == 1)
			{
				repeat = false;

				return true;
			}

			boolean result = !ForgeEventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_AIR, 0, 0, 0, -1).isCanceled();
			if (result && stack != null && Minecraft.getMinecraft().playerController.sendUseItem(player, player.worldObj, stack))
			{
				if (offHand)
				{
					data.resetEquippedProgress();
					PacketDispatcher.sendPacketToServer(EnumPacketTypes.populatePacket(new PacketSetData(player, data, true)));
				}
				else
				{
					Minecraft.getMinecraft().entityRenderer.itemRenderer.resetEquippedProgress();
				}

				repeat = true;

				return false;
			}

			repeat = false;

			return true;
		}

		int x = target.blockX;
		int y = target.blockY;
		int z = target.blockZ;
		int side = target.sideHit;

		if (stack == null)
		{
			if (target.typeOfHit == EnumMovingObjectType.TILE)
			{
				Minecraft.getMinecraft().playerController.clickBlock(x, y, z, side);

				return true;
			}
		}

		else
		{
			switch (target.typeOfHit)
			{
				case TILE:

					if (click == 1)
					{
						if (stack.getItem() instanceof ItemTool)
						{
							Minecraft.getMinecraft().playerController.clickBlock(x, y, z, side);
						}

						repeat = true;

						return true;
					}

					boolean result = !ForgeEventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_BLOCK, x, y, z, side).isCanceled();
					boolean bool = Minecraft.getMinecraft().playerController.onPlayerRightClick(player, player.worldObj, stack, x, y, z, side, target.hitVec);

					if (result && bool)
					{
						if (stack.stackSize == 0)
						{
							stack = null;
						}

						repeat = true;

						return true;
					}

					else
					{
						boolean result2 = !ForgeEventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_AIR, 0, 0, 0, -1).isCanceled();
						if (result2 && stack != null && Minecraft.getMinecraft().playerController.sendUseItem(player, player.worldObj, stack))
						{
							if (offHand)
							{
								data.resetEquippedProgress();
								PacketDispatcher.sendPacketToServer(EnumPacketTypes.populatePacket(new PacketSetData(player, data, true)));
							}
							else
							{
								Minecraft.getMinecraft().entityRenderer.itemRenderer.resetEquippedProgress();
							}

							repeat = true;

							return false;
						}

						repeat = false;

						return false;
					}

				case ENTITY:

					if (click == 0 && Minecraft.getMinecraft().playerController.func_78768_b(player, target.entityHit))
					{
						return true;
					}

					if (click == 1)
					{
						Minecraft.getMinecraft().playerController.attackEntity(player, target.entityHit);

						return true;
					}

					break;
			}
		}

		return false;
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
