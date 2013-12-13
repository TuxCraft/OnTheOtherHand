package mcdelta.ooh;

import mcdelta.ooh.client.DualWieldRenderHandler;
import mcdelta.ooh.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod (modid = OOH.modid, useMetadata = true, version = "0.1")
@NetworkMod (clientSideRequired = true, serverSideRequired = false)
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
		MinecraftForge.EVENT_BUS.register(new DualWieldRenderHandler());
	}
}
