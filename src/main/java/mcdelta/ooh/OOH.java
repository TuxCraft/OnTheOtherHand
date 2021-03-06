package mcdelta.ooh;

import java.util.ArrayList;
import java.util.List;

import mcdelta.ooh.client.FirstPersonRenderHandler;
import mcdelta.ooh.client.HotbarOverlayHandler;
import mcdelta.ooh.client.ThirdPersonRenderHandler;
import mcdelta.ooh.handler.PlayerTickHandler;
import mcdelta.ooh.handler.PlayerTracker;
import mcdelta.ooh.network.PacketHandler;
import mcdelta.ooh.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod (modid = OOH.modid, useMetadata = true, name = "On the Other Hand", version = "0.0d.pre")
@NetworkMod (clientSideRequired = true, serverSideRequired = false, channels =
{ OOH.modid }, packetHandler = PacketHandler.class)
public class OOH
{
	// TODO
	// - wierd click after disengaging dual wield
	// - right arm still renders at times
	// - doesn't swing @ times

	@Instance (OOH.modid)
	public static OOH	       instance;
	@SidedProxy (clientSide = "mcdelta.ooh.proxy.ClientProxy", serverSide = "mcdelta.ooh.proxy.CommonProxy")
	public static CommonProxy	proxy;
	public static final String	modid	     = "OOH";
	public static boolean	   isObfuscated	 = false;
	public static List<Item>	illegalItems	= new ArrayList();




	@EventHandler
	public void load (FMLInitializationEvent event)
	{
		proxy.registerKeyBinds();

		if (isClient())
		{
			GameSettings settings = Minecraft.getMinecraft().gameSettings;
			settings.keyBindAttack = new KeyBinding("key.attack", -100);
			settings.keyBindUseItem = new KeyBinding("key.use", -99);

			MinecraftForge.EVENT_BUS.register(new ThirdPersonRenderHandler());
			MinecraftForge.EVENT_BUS.register(new FirstPersonRenderHandler());
			MinecraftForge.EVENT_BUS.register(new HotbarOverlayHandler());
		}

		TickRegistry.registerTickHandler(new PlayerTickHandler(), Side.CLIENT);
		TickRegistry.registerTickHandler(new PlayerTickHandler(), Side.SERVER);
		GameRegistry.registerPlayerTracker(new PlayerTracker());
		
		illegalItems.add(Item.map);
	}




	public static void log (Object message)
	{
		System.out.println(message);
	}




	public static boolean isClient ()
	{
		return FMLCommonHandler.instance().getEffectiveSide().isClient();
	}




	public static boolean isServer ()
	{
		return FMLCommonHandler.instance().getEffectiveSide().isServer();
	}




	public static boolean idMetaDamageMatch (ItemStack stack1, ItemStack stack2)
	{
		return stack1.itemID == stack2.itemID && stack1.getItemDamage() == stack2.getItemDamage() && stack1.stackSize == stack2.stackSize;
	}




	public static int getArmSwingAnimationEnd (EntityPlayer player)
	{
		return player.isPotionActive(Potion.digSpeed) ? 6 - (1 + player.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1 : (player.isPotionActive(Potion.digSlowdown) ? 6 + (1 + player.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2 : 6);
	}
}
