package mcdelta.ooh;

import mcdelta.ooh.client.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.network.NetworkMod;

@Mod (modid = OOH.modid, useMetadata = true)
@NetworkMod (clientSideRequired = true, serverSideRequired = false)
public class OOH
{
	@Instance (OOH.modid)
	public static OOH	       instance;

	@SidedProxy (clientSide = "mdelta.ooh.proxy.ClientProxy", serverSide = "mdelta.ooh.proxy.CommonProxy")
	public static CommonProxy	proxy;

	public static final String	modid	= "OOH";
}
