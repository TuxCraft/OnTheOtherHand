package mcdelta.ooh.client;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ThirdPersonRenderHandler
{
	private static final ResourceLocation	RES_ITEM_GLINT	       = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	private static final ResourceLocation	RES_MAP_BACKGROUND	   = new ResourceLocation("textures/map/map_background.png");
	private static final ResourceLocation	RES_UNDERWATER_OVERLAY	= new ResourceLocation("textures/misc/underwater.png");

	private RenderBlocks	              renderBlocks	           = new RenderBlocks();
	private RenderManager	              renderManager;
	private ModelBiped	                  modelBipedMain;
	private ModelRenderer	              arm;




	@ForgeSubscribe
	public void renderSecondHand (RenderPlayerEvent.Pre event)
	{
		try
		{
			EntityPlayer player = event.entityPlayer;
			RenderPlayer renderer = event.renderer;

			Field fi1 = renderer.getClass().getDeclaredField("modelBipedMain");
			fi1.setAccessible(true);
			modelBipedMain = (ModelBiped) fi1.get(renderer);

			Field fi2 = renderer.getClass().getSuperclass().getSuperclass().getDeclaredField("renderManager");
			fi2.setAccessible(true);
			renderManager = (RenderManager) fi2.get(renderer);

			arm = new ModelRenderer(modelBipedMain, 40, 16);
			arm.mirror = true;
			arm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
			arm.setRotationPoint(5.0F, 2.0F + 0.0F, 0.0F);

			arm.rotateAngleX = modelBipedMain.bipedLeftArm.rotateAngleX;
			arm.rotateAngleY = modelBipedMain.bipedLeftArm.rotateAngleY;
			arm.rotateAngleZ = modelBipedMain.bipedLeftArm.rotateAngleZ;

			if (renderSwingProgress(player, event.partialRenderTick) > -9990.0F)
			{
				float f6 = renderSwingProgress(player, event.partialRenderTick);
				modelBipedMain.bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(f6) * (float) Math.PI * 2.0F) * 0.2F;
				modelBipedMain.bipedRightArm.rotationPointZ = MathHelper.sin(modelBipedMain.bipedBody.rotateAngleY) * 5.0F;
				modelBipedMain.bipedRightArm.rotationPointX = -MathHelper.cos(modelBipedMain.bipedBody.rotateAngleY) * 5.0F;
				arm.rotateAngleY -= modelBipedMain.bipedBody.rotateAngleY;
				modelBipedMain.bipedRightArm.rotateAngleY -= modelBipedMain.bipedBody.rotateAngleY;
				modelBipedMain.bipedRightArm.rotateAngleX -= modelBipedMain.bipedBody.rotateAngleY;
				f6 = 1.0F - renderSwingProgress(player, event.partialRenderTick);
				f6 *= f6;
				f6 *= f6;
				f6 = 1.0F - f6;
				float f7 = MathHelper.sin(f6 * (float) Math.PI);
				float f8 = MathHelper.sin(renderSwingProgress(player, event.partialRenderTick) * (float) Math.PI) * -(modelBipedMain.bipedHead.rotateAngleX - 0.7F) * 0.75F;
				arm.rotateAngleX = (float) ((double) arm.rotateAngleX - ((double) f7 * 1.2D + (double) f8));
				arm.rotateAngleY -= modelBipedMain.bipedBody.rotateAngleY * 2.0F;
				arm.rotateAngleZ = -MathHelper.sin(renderSwingProgress(player, event.partialRenderTick) * (float) Math.PI) * -0.4F;
			}

			arm.render(0.0625F);

			modelBipedMain.bipedLeftArm.showModel = true;
			modelBipedMain.bipedLeftArm.rotateAngleX = arm.rotateAngleX;
			modelBipedMain.bipedLeftArm.rotateAngleY = arm.rotateAngleY;
			modelBipedMain.bipedLeftArm.rotateAngleZ = arm.rotateAngleZ;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}




	@ForgeSubscribe
	public void renderSecondHandItem (RenderPlayerEvent.Specials.Post event)
	{
		/**EntityPlayer player = event.entityPlayer;
		ItemStack stack = player.inventory.getCurrentItem();

		if (stack != null)
		{
			GL11.glPushMatrix();
			arm.postRender(0.0625F);
			GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);

			if (player.fishEntity != null)
			{
				stack = new ItemStack(Item.stick);
			}

			EnumAction enumaction = null;

			if (player.getItemInUseCount() > 0)
			{
				enumaction = stack.getItemUseAction();
			}

			float f11;

			IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(stack, EQUIPPED);
			boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, stack, BLOCK_3D));
			boolean isBlock = stack.itemID < Block.blocksList.length && stack.getItemSpriteNumber() == 0;

			if (is3D || (isBlock && RenderBlocks.renderItemIn3d(Block.blocksList[stack.itemID].getRenderType())))
			{
				f11 = 0.5F;
				GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
				f11 *= 0.75F;
				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(-f11, -f11, f11);
			}
			else if (stack.itemID == Item.bow.itemID)
			{
				f11 = 0.625F;
				GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
				GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(f11, -f11, f11);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			}
			else if (Item.itemsList[stack.itemID].isFull3D())
			{
				f11 = 0.625F;

				if (Item.itemsList[stack.itemID].shouldRotateAroundWhenRendering())
				{
					GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
					GL11.glTranslatef(0.0F, -0.125F, 0.0F);
				}

				if (player.getItemInUseCount() > 0 && enumaction == EnumAction.block)
				{
					GL11.glTranslatef(0.05F, 0.0F, -0.1F);
					GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
				}

				GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
				GL11.glScalef(f11, -f11, f11);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			}
			else
			{
				f11 = 0.375F;
				GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
				GL11.glScalef(f11, f11, f11);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}

			float f12;
			float f13;
			int j;

			if (stack.getItem().requiresMultipleRenderPasses())
			{
				for (j = 0; j < stack.getItem().getRenderPasses(stack.getItemDamage()); ++j)
				{
					int k = stack.getItem().getColorFromItemStack(stack, j);
					f13 = (float) (k >> 16 & 255) / 255.0F;
					f12 = (float) (k >> 8 & 255) / 255.0F;
					float f6 = (float) (k & 255) / 255.0F;
					GL11.glColor4f(f13, f12, f6, 1.0F);
					renderManager.itemRenderer.renderItem(player, stack, j);
				}
			}
			else
			{
				j = stack.getItem().getColorFromItemStack(stack, 0);
				float f14 = (float) (j >> 16 & 255) / 255.0F;
				f13 = (float) (j >> 8 & 255) / 255.0F;
				f12 = (float) (j & 255) / 255.0F;
				GL11.glColor4f(f14, f13, f12, 1.0F);
				renderManager.itemRenderer.renderItem(player, stack, 0);
			}

			GL11.glPopMatrix();
		}*/
	}




	public void renderItem (EntityLivingBase par1EntityLivingBase, ItemStack par2ItemStack, int par3, ItemRenderType type)
	{
		GL11.glPushMatrix();
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();

		Block block = null;
		if (par2ItemStack.getItem() instanceof ItemBlock && par2ItemStack.itemID < Block.blocksList.length)
		{
			block = Block.blocksList[par2ItemStack.itemID];
		}

		IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(par2ItemStack, type);
		if (customRenderer != null)
		{
			texturemanager.bindTexture(texturemanager.getResourceLocation(par2ItemStack.getItemSpriteNumber()));
			ForgeHooksClient.renderEquippedItem(type, customRenderer, renderBlocks, par1EntityLivingBase, par2ItemStack);
		}
		else if (block != null && par2ItemStack.getItemSpriteNumber() == 0 && RenderBlocks.renderItemIn3d(Block.blocksList[par2ItemStack.itemID].getRenderType()))
		{
			texturemanager.bindTexture(texturemanager.getResourceLocation(0));
			renderBlocks.renderBlockAsItem(Block.blocksList[par2ItemStack.itemID], par2ItemStack.getItemDamage(), 1.0F);
		}
		else
		{
			Icon icon = par1EntityLivingBase.getItemIcon(par2ItemStack, par3);

			if (icon == null)
			{
				GL11.glPopMatrix();
				return;
			}

			texturemanager.bindTexture(texturemanager.getResourceLocation(par2ItemStack.getItemSpriteNumber()));
			Tessellator tessellator = Tessellator.instance;
			float f = icon.getMinU();
			float f1 = icon.getMaxU();
			float f2 = icon.getMinV();
			float f3 = icon.getMaxV();
			float f4 = 0.0F;
			float f5 = 0.3F;
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glTranslatef(-f4, -f5, 0.0F);
			float f6 = 1.5F;
			GL11.glScalef(f6, f6, f6);
			GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
			renderItemIn2D(tessellator, f1, f2, f, f3, icon.getIconWidth(), icon.getIconHeight(), 0.0625F);

			if (par2ItemStack.hasEffect(par3))
			{
				GL11.glDepthFunc(GL11.GL_EQUAL);
				GL11.glDisable(GL11.GL_LIGHTING);
				texturemanager.bindTexture(RES_ITEM_GLINT);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
				float f7 = 0.76F;
				GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glPushMatrix();
				float f8 = 0.125F;
				GL11.glScalef(f8, f8, f8);
				float f9 = (float) (Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
				GL11.glTranslatef(f9, 0.0F, 0.0F);
				GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
				renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
				GL11.glPopMatrix();
				GL11.glPushMatrix();
				GL11.glScalef(f8, f8, f8);
				f9 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
				GL11.glTranslatef(-f9, 0.0F, 0.0F);
				GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
				renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
				GL11.glPopMatrix();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}

		GL11.glPopMatrix();
	}




	public static void renderItemIn2D (Tessellator par0Tessellator, float par1, float par2, float par3, float par4, int par5, int par6, float par7)
	{
		par0Tessellator.startDrawingQuads();
		par0Tessellator.setNormal(0.0F, 0.0F, 1.0F);
		par0Tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, (double) par1, (double) par4);
		par0Tessellator.addVertexWithUV(1.0D, 0.0D, 0.0D, (double) par3, (double) par4);
		par0Tessellator.addVertexWithUV(1.0D, 1.0D, 0.0D, (double) par3, (double) par2);
		par0Tessellator.addVertexWithUV(0.0D, 1.0D, 0.0D, (double) par1, (double) par2);
		par0Tessellator.draw();
		par0Tessellator.startDrawingQuads();
		par0Tessellator.setNormal(0.0F, 0.0F, -1.0F);
		par0Tessellator.addVertexWithUV(0.0D, 1.0D, (double) (0.0F - par7), (double) par1, (double) par2);
		par0Tessellator.addVertexWithUV(1.0D, 1.0D, (double) (0.0F - par7), (double) par3, (double) par2);
		par0Tessellator.addVertexWithUV(1.0D, 0.0D, (double) (0.0F - par7), (double) par3, (double) par4);
		par0Tessellator.addVertexWithUV(0.0D, 0.0D, (double) (0.0F - par7), (double) par1, (double) par4);
		par0Tessellator.draw();
		float f5 = 0.5F * (par1 - par3) / (float) par5;
		float f6 = 0.5F * (par4 - par2) / (float) par6;
		par0Tessellator.startDrawingQuads();
		par0Tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		int k;
		float f7;
		float f8;

		for (k = 0; k < par5; ++k)
		{
			f7 = (float) k / (float) par5;
			f8 = par1 + (par3 - par1) * f7 - f5;
			par0Tessellator.addVertexWithUV((double) f7, 0.0D, (double) (0.0F - par7), (double) f8, (double) par4);
			par0Tessellator.addVertexWithUV((double) f7, 0.0D, 0.0D, (double) f8, (double) par4);
			par0Tessellator.addVertexWithUV((double) f7, 1.0D, 0.0D, (double) f8, (double) par2);
			par0Tessellator.addVertexWithUV((double) f7, 1.0D, (double) (0.0F - par7), (double) f8, (double) par2);
		}

		par0Tessellator.draw();
		par0Tessellator.startDrawingQuads();
		par0Tessellator.setNormal(1.0F, 0.0F, 0.0F);
		float f9;

		for (k = 0; k < par5; ++k)
		{
			f7 = (float) k / (float) par5;
			f8 = par1 + (par3 - par1) * f7 - f5;
			f9 = f7 + 1.0F / (float) par5;
			par0Tessellator.addVertexWithUV((double) f9, 1.0D, (double) (0.0F - par7), (double) f8, (double) par2);
			par0Tessellator.addVertexWithUV((double) f9, 1.0D, 0.0D, (double) f8, (double) par2);
			par0Tessellator.addVertexWithUV((double) f9, 0.0D, 0.0D, (double) f8, (double) par4);
			par0Tessellator.addVertexWithUV((double) f9, 0.0D, (double) (0.0F - par7), (double) f8, (double) par4);
		}

		par0Tessellator.draw();
		par0Tessellator.startDrawingQuads();
		par0Tessellator.setNormal(0.0F, 1.0F, 0.0F);

		for (k = 0; k < par6; ++k)
		{
			f7 = (float) k / (float) par6;
			f8 = par4 + (par2 - par4) * f7 - f6;
			f9 = f7 + 1.0F / (float) par6;
			par0Tessellator.addVertexWithUV(0.0D, (double) f9, 0.0D, (double) par1, (double) f8);
			par0Tessellator.addVertexWithUV(1.0D, (double) f9, 0.0D, (double) par3, (double) f8);
			par0Tessellator.addVertexWithUV(1.0D, (double) f9, (double) (0.0F - par7), (double) par3, (double) f8);
			par0Tessellator.addVertexWithUV(0.0D, (double) f9, (double) (0.0F - par7), (double) par1, (double) f8);
		}

		par0Tessellator.draw();
		par0Tessellator.startDrawingQuads();
		par0Tessellator.setNormal(0.0F, -1.0F, 0.0F);

		for (k = 0; k < par6; ++k)
		{
			f7 = (float) k / (float) par6;
			f8 = par4 + (par2 - par4) * f7 - f6;
			par0Tessellator.addVertexWithUV(1.0D, (double) f7, 0.0D, (double) par3, (double) f8);
			par0Tessellator.addVertexWithUV(0.0D, (double) f7, 0.0D, (double) par1, (double) f8);
			par0Tessellator.addVertexWithUV(0.0D, (double) f7, (double) (0.0F - par7), (double) par1, (double) f8);
			par0Tessellator.addVertexWithUV(1.0D, (double) f7, (double) (0.0F - par7), (double) par3, (double) f8);
		}

		par0Tessellator.draw();
	}




	protected float renderSwingProgress (EntityLivingBase living, float f)
	{
		return living.getSwingProgress(f);
	}
}
