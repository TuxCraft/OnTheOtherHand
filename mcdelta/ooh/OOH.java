package mcdelta.ooh;

import mcdelta.ooh.handler.PlayerHandler;
import mcdelta.ooh.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod (modid = OOH.modid, useMetadata = true, name = "On the Other Hand", version = "0.1")
@NetworkMod (clientSideRequired = true, serverSideRequired = false, channels = { OOH.modid })
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
		
		GameRegistry.registerPlayerTracker(new PlayerHandler());
	}
}
