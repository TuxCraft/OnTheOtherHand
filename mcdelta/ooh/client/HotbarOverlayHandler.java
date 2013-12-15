package mcdelta.ooh.client;

import static mcdelta.ooh.OOH.*;

import mcdelta.ooh.OOHData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class HotbarOverlayHandler
{
	private static final ResourceLocation	WIDGITS	   = new ResourceLocation("textures/gui/widgets.png");
	protected static final RenderItem	  itemRenderer	= new RenderItem();
	private int	                          zLevel	   = 100;




	@ForgeSubscribe
	public void renderOverlay (RenderGameOverlayEvent.Post event)
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		OOHData data = OOHData.getOOHData(player);

		if (data != null && event.type == ElementType.HOTBAR && data.doubleEngaged)
		{
			int height = event.resolution.getScaledHeight();
			int width = event.resolution.getScaledWidth();
			
			int slot = (player.inventory.currentItem - 1 < 0) ? 8 : player.inventory.currentItem - 1;
			
			Minecraft.getMinecraft().renderEngine.bindTexture(WIDGITS);
			drawTexturedModalRect(width / 2 - 91 - 1 + slot * 20, height - 22 - 1, 0, 22, 24, 22);
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




	protected void renderInventorySlot (int par1, int par2, int par3, float par4)
	{
		ItemStack itemstack = Minecraft.getMinecraft().thePlayer.inventory.mainInventory[par1];

		if (itemstack != null)
		{
			float f1 = (float) itemstack.animationsToGo - par4;

			if (f1 > 0.0F)
			{
				GL11.glPushMatrix();
				float f2 = 1.0F + f1 / 5.0F;
				GL11.glTranslatef((float) (par2 + 8), (float) (par3 + 12), 0.0F);
				GL11.glScalef(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
				GL11.glTranslatef((float) (-(par2 + 8)), (float) (-(par3 + 12)), 0.0F);
			}

			log(itemstack);
			itemRenderer.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), itemstack, par2, par3);

			if (f1 > 0.0F)
			{
				GL11.glPopMatrix();
			}

			itemRenderer.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().getTextureManager(), itemstack, par2, par3);
		}
	}
}
