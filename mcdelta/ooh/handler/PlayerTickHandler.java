package mcdelta.ooh.handler;

import static mcdelta.ooh.OOH.*;

import java.util.EnumSet;

import mcdelta.ooh.OOHData;
import mcdelta.ooh.network.EnumPacketTypes;
import mcdelta.ooh.network.PacketSetData;
import mcdelta.ooh.network.PacketStartSwing;
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
							leftClick = settings.keyBindAttack;
							rightClick = settings.keyBindUseItem;

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
					if (isServer())
					{
						
					}

					if (isClient())
					{
						data.swingProgress[1] = data.swingProgress[0];

						if (Minecraft.getMinecraft().thePlayer.username.equals(player.username))
						{
							int slot = (player.inventory.currentItem - 1 < 0) ? 8 : player.inventory.currentItem - 1;

							ItemStack stack1 = data.secondItem;
							ItemStack stack2 = player.inventory.getStackInSlot(slot);

							boolean idsMatch = false;
							boolean metaMatch = false;
							boolean sizeMatch = false;
							boolean bool = stack1 != stack2;

							if (stack1 != null && stack2 != null)
							{
								idsMatch = stack1.itemID == stack2.itemID;
								metaMatch = stack1.getItemDamage() == stack2.getItemDamage();
								sizeMatch = stack1.stackSize == stack2.stackSize;
								bool = true;
							}

							if ((!(idsMatch && metaMatch && sizeMatch) && bool))
							{
								data.secondItem = player.inventory.getStackInSlot(slot);
								data.startSwing = false;
								data.resetEquippedProgress();
								PacketDispatcher.sendPacketToServer(EnumPacketTypes.populatePacket(new PacketSetData(player, data, true)));
							}
							
							
							
							
							
							
							if (cooldownRight != 0)
							{
								cooldownRight--;
							}

							if (cooldownLeft != 0)
							{
								cooldownLeft--;
							}

							if (rightClick != null && rightClick.pressed)
							{
								rightHeldTime++;
							}

							else
							{
								rightHeldTime = 0;
							}

							if (leftClick != null && leftClick.pressed)
							{
								leftHeldTime++;
							}

							else
							{
								leftHeldTime = 0;
							}

							if (rightHeldTime >= 1)
							{
								int orig = player.inventory.currentItem;
								player.inventory.currentItem = slot;

								int i = data.secondItem != null && data.secondItem.getItem() != null && (data.secondItem.getItem() instanceof ItemTool || data.secondItem.getItem() instanceof ItemSword) ? 1 : 0;

								if (cooldownRight == 0)
								{
									cooldownRight = 4;

									if (click(player, i, true))
									{
										data.startSwing = true;
										PacketDispatcher.sendPacketToServer(EnumPacketTypes.populatePacket(new PacketStartSwing(player)));
									}
								}

								if (i == 1)
								{
									sendClickBlockToController(0, Minecraft.getMinecraft().currentScreen == null && Minecraft.getMinecraft().inGameHasFocus);
								}

								player.inventory.currentItem = orig;
							}

							if (leftHeldTime >= 1)
							{
								int i = player.getCurrentEquippedItem() != null && (player.getCurrentEquippedItem().getItem() instanceof ItemTool || player.getCurrentEquippedItem().getItem() instanceof ItemSword) ? 1 : 0;

								if (cooldownLeft == 0)
								{
									cooldownLeft = 4;

									if (click(player, i, false))
									{
										player.swingItem();
									}
								}

								if (i == 1)
								{
									sendClickBlockToController(0, Minecraft.getMinecraft().currentScreen == null && Minecraft.getMinecraft().inGameHasFocus);
								}
							}

							data.equipProgress[1] = data.equipProgress[0];

							float f = 0.5F;

							float f1 = 1.0F - data.equipProgress[0];

							if (f1 < -f)
							{
								f1 = -f;
							}

							if (f1 > f)
							{
								f1 = f;
							}

							data.equipProgress[0] += f1;

							if (data.equipProgress[0] != 1)
							{
								PacketDispatcher.sendPacketToServer(EnumPacketTypes.populatePacket(new PacketSetData(player, data, true)));
							}
						}

						if (data.startSwing)
						{
							data.startSwing = false;
							data.swingArm(player);
						}

						data.updateArmSwing(player);
						OOHData.setOOHData(player, data);
					}
				}

				else
				{
					if (isClient())
					{
						if (Minecraft.getMinecraft().thePlayer.username.equals(player.username))
						{
							GameSettings settings = Minecraft.getMinecraft().gameSettings;

							if (leftClick != null && rightClick != null)
							{
								settings.keyBindAttack = leftClick;
								settings.keyBindUseItem = rightClick;
							}
						}
					}
				}
			}
		}
	}




	private void sendClickBlockToController (int par1, boolean par2)
	{
		if (par2 && Minecraft.getMinecraft().objectMouseOver != null && Minecraft.getMinecraft().objectMouseOver.typeOfHit == EnumMovingObjectType.TILE && par1 == 0)
		{
			int j = Minecraft.getMinecraft().objectMouseOver.blockX;
			int k = Minecraft.getMinecraft().objectMouseOver.blockY;
			int l = Minecraft.getMinecraft().objectMouseOver.blockZ;
			Minecraft.getMinecraft().playerController.onPlayerDamageBlock(j, k, l, Minecraft.getMinecraft().objectMouseOver.sideHit);

			if (Minecraft.getMinecraft().thePlayer.isCurrentToolAdventureModeExempt(j, k, l))
			{
				Minecraft.getMinecraft().effectRenderer.addBlockHitEffects(j, k, l, Minecraft.getMinecraft().objectMouseOver);
			}
		}
		else
		{
			Minecraft.getMinecraft().playerController.resetBlockRemoving();
		}
	}




	private boolean click (EntityPlayer player, int click, boolean offHand)
	{
		ItemStack stack = player.getCurrentEquippedItem();
		MovingObjectPosition target = Minecraft.getMinecraft().objectMouseOver;
		OOHData data = OOHData.getOOHData(player);

		if (target == null)
		{
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

				return false;
			}

			return stack == null;
		}

		if (stack == null)
		{
			if (target.typeOfHit == EnumMovingObjectType.TILE)
			{
				return true;
			}
		}

		else
		{
			switch (target.typeOfHit)
			{
				case TILE:

					int x = target.blockX;
					int y = target.blockY;
					int z = target.blockZ;
					int side = target.sideHit;

					boolean result = !ForgeEventFactory.onPlayerInteract(player, Action.RIGHT_CLICK_BLOCK, x, y, z, side).isCanceled();
					boolean bool = Minecraft.getMinecraft().playerController.onPlayerRightClick(player, player.worldObj, stack, x, y, z, side, target.hitVec);

					if (result && bool)
					{
						if (stack.stackSize == 0)
						{
							stack = null;
						}

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

							return false;
						}

						return false;
					}

				case ENTITY:

					if (Minecraft.getMinecraft().playerController.func_78768_b(player, target.entityHit))
					{
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
