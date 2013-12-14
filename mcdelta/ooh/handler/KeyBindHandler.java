package mcdelta.ooh.handler;

import static mcdelta.ooh.OOH.log;

import java.util.EnumSet;

import mcdelta.ooh.OOHData;
import mcdelta.ooh.network.EnumPacketTypes;
import mcdelta.ooh.network.PacketSetData;
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
		if (kb.keyDescription.contains("activateDualWield") && tickEnd)
		{
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			OOHData data = OOHData.getOOHData(player);
			
			log(data.doubleEngaged);
			
			if(data.doubleEngaged)
			{
				data.doubleEngaged = false;
			}
			else
			{
				player.inventory.currentItem = 0;
				data.doubleEngaged = true;
			}
			
			OOHData.setOOHData(player, data);
			PacketDispatcher.sendPacketToServer(EnumPacketTypes.populatePacket(new PacketSetData(player, OOHData.getOOHData(player), true)));
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
