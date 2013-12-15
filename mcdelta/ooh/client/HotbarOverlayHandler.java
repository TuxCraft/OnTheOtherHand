package mcdelta.ooh.client;

import mcdelta.ooh.OOHData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class HotbarOverlayHandler
{
	private static final ResourceLocation	OVERLAY	= new ResourceLocation("ooh", "textures/gui/overlay.png");
	private static final ResourceLocation	WIDGITS	= new ResourceLocation("textures/gui/widgets.png");
	private int	                          zLevel	= 100;




	@ForgeSubscribe
	public void renderOverlay (RenderGameOverlayEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		OOHData data = OOHData.getOOHData(player);
		
		if (data != null && event.type == ElementType.HOTBAR && data.doubleEngaged)
		{
			event.setCanceled(true);
			
			float height = event.resolution.getScaledHeight();
			float width = event.resolution.getScaledWidth();
					
			Minecraft.getMinecraft().mcProfiler.startSection("actionBar");

	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        Minecraft.getMinecraft().renderEngine.bindTexture(WIDGITS);

	        InventoryPlayer inv = Minecraft.getMinecraft().thePlayer.inventory;
	        drawTexturedModalRect(width / 2 - 91, height - 22, 0, 0, 182, 22);
	        drawTexturedModalRect(width / 2 - 91 - 1 + inv.currentItem * 20, height - 22 - 1, 0, 22, 24, 22);

	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	        RenderHelper.enableGUIStandardItemLighting();

	        for (int i = 0; i < 9; ++i)
	        {
	            int x = width / 2 - 90 + i * 20 + 2;
	            int z = height - 16 - 3;
	            renderInventorySlot(i, x, z, event.partialTicks);
	        }

	        RenderHelper.disableStandardItemLighting();
	        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	        Minecraft.getMinecraft().mcProfiler.endSection();
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
