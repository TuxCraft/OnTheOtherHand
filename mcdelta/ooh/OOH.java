package mcdelta.ooh;

import mcdelta.ooh.handler.PlayerTickHandler;
import mcdelta.ooh.handler.PlayerTracker;
import mcdelta.ooh.proxy.CommonProxy;
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

@Mod (modid = OOH.modid, useMetadata = true, name = "On the Other Hand", version = "0.1")
@NetworkMod (clientSideRequired = true, serverSideRequired = false, channels =
{ OOH.modid })
public class OOH
{
	@Instance (OOH.modid)
	public static OOH	       instance;

	@SidedProxy (clientSide = "mcdelta.ooh.proxy.ClientProxy", serverSide = "mcdelta.ooh.proxy.CommonProxy")
	public static CommonProxy	proxy;

	public static final String	modid	= "OOH";




	@EventHandler
	public void load (FMLInitializationEvent event)
	{
		proxy.registerKeyBinds();

		TickRegistry.registerTickHandler(new PlayerTickHandler(), Side.CLIENT);
		TickRegistry.registerTickHandler(new PlayerTickHandler(), Side.SERVER);
		GameRegistry.registerPlayerTracker(new PlayerTracker());
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
}
