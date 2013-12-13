package mcdelta.ooh;

import java.util.EnumSet;

import mcdelta.ooh.network.EnumPacketTypes;
import mcdelta.ooh.network.PacketUpdateTwoItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class KeyBindHandler extends KeyHandler
{

	public KeyBindHandler (KeyBinding[] keyBindings)
	{
		super(keyBindings, new boolean[]
		{ false });
	}




	@Override
	public String getLabel ()
	{
		return "OOH";
	}




	@Override
	public void keyDown (EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
	{
		if (kb.keyDescription.contains("activateDualWield"))
		{
			if (Assets.isClient() && tickEnd)
			{
				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				
				if (NBTHelper.holdingTwo(player))
				{
					NBTHelper.setHoldingTwo(player, false);
					PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketUpdateTwoItems(false)));
					return;
				}

				player.inventory.currentItem = 8;
				NBTHelper.setHoldingTwo(player, true);
				PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketUpdateTwoItems(true)));
			}
		}
	}




	@Override
	public void keyUp (EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
	{

	}




	@Override
	public EnumSet<TickType> ticks ()
	{
		return EnumSet.of(TickType.PLAYER);
	}

}
