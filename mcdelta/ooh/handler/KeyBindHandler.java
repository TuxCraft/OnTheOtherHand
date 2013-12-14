package mcdelta.ooh.handler;

import java.util.EnumSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

import static mcdelta.ooh.OOH.*;

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
