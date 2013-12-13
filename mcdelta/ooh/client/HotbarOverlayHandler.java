package mcdelta.ooh.client;

import mcdelta.ooh.OOH;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.ForgeSubscribe;

public class HotbarOverlayHandler
{
	private static final ResourceLocation	OVERLAY	= new ResourceLocation(OOH.modid, "textures/gui/overlay.png");
	private int	                          zLevel	= 100;




	@ForgeSubscribe
	public void renderOverlay (RenderGameOverlayEvent event)
	{
		if (event.type == ElementType.HOTBAR)
		{
			Minecraft.getMinecraft().renderEngine.bindTexture(OVERLAY);
			drawTexturedModalRect(event.resolution.getScaledWidth() / 2 - 91, event.resolution.getScaledHeight() - 22, 0, 0, 182, 22);
		}
	}




	public void drawTexturedModalRect (int par1, int par2, int par3, int par4, int par5, int par6)
	{
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 + par6), (double) this.zLevel, (double) ((float) (par3 + 0) * f), (double) ((float) (par4 + par6) * f1));
		tessellator.addVertexWithUV((double) (par1 + par5), (double) (par2 + par6), (double) this.zLevel, (double) ((float) (par3 + par5) * f), (double) ((float) (par4 + par6) * f1));
		tessellator.addVertexWithUV((double) (par1 + par5), (double) (par2 + 0), (double) this.zLevel, (double) ((float) (par3 + par5) * f), (double) ((float) (par4 + 0) * f1));
		tessellator.addVertexWithUV((double) (par1 + 0), (double) (par2 + 0), (double) this.zLevel, (double) ((float) (par3 + 0) * f), (double) ((float) (par4 + 0) * f1));
		tessellator.draw();
	}
}
