package mcdelta.ooh;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

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
					return;
				}

				player.inventory.currentItem = 8;
				NBTHelper.setHoldingTwo(player, true);
				NBTHelper.setNBT(player, NBTHelper.WEAPON_PROG, 0.1F);
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
