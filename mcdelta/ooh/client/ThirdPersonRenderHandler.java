package mcdelta.ooh.client;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import mcdelta.ooh.OOHData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

import static mcdelta.ooh.OOH.*;

public class ThirdPersonRenderHandler
{
	private static final ResourceLocation	RES_ITEM_GLINT	       = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	private static final ResourceLocation	RES_MAP_BACKGROUND	   = new ResourceLocation("textures/map/map_background.png");
	private static final ResourceLocation	RES_UNDERWATER_OVERLAY	= new ResourceLocation("textures/misc/underwater.png");

	private RenderManager	              renderManager;
	private ModelBiped	                  modelArmorChestplate;
	private ModelBiped	                  modelBipedMain;
	private Method	                      getEntityTexture;
	private ModelRenderer	              arm;




	@ForgeSubscribe
	public void renderSecondHand (RenderPlayerEvent.Pre event)
	{
		EntityPlayer player = event.entityPlayer;
		OOHData data = OOHData.getOOHData(player);
		RenderPlayer renderer = event.renderer;

		try
		{
			if (modelBipedMain == null)
			{
				Field fi1 = renderer.getClass().getDeclaredField("modelBipedMain");
				fi1.setAccessible(true);
				modelBipedMain = (ModelBiped) fi1.get(renderer);

				Field fi2 = renderer.getClass().getDeclaredField("modelArmorChestplate");
				fi2.setAccessible(true);
				modelArmorChestplate = (ModelBiped) fi2.get(renderer);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		ItemStack stack = data.doubleEngaged ? data.secondItem : null;
		modelBipedMain.heldItemLeft = modelArmorChestplate.heldItemLeft = stack == null ? 0 : 1;
	}




	@ForgeSubscribe
	public void renderSecondHand (RenderPlayerEvent.Specials.Pre event)
	{
		EntityPlayer player = event.entityPlayer;
		OOHData data = OOHData.getOOHData(player);
		RenderPlayer renderer = event.renderer;

		try
		{
			if (modelBipedMain == null)
			{
				Field fi1 = renderer.getClass().getDeclaredField("modelBipedMain");
				fi1.setAccessible(true);
				modelBipedMain = (ModelBiped) fi1.get(renderer);

				Field fi2 = renderer.getClass().getDeclaredField("modelArmorChestplate");
				fi2.setAccessible(true);
				modelArmorChestplate = (ModelBiped) fi2.get(renderer);
			}

			if (renderManager == null)
			{
				Field fi1 = renderer.getClass().getSuperclass().getSuperclass().getDeclaredField("renderManager");
				fi1.setAccessible(true);
				renderManager = (RenderManager) fi1.get(renderer);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (data != null)
		{
			arm = new ModelRenderer(modelBipedMain, 40, 16);
			arm.mirror = true;
			arm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
			arm.setRotationPoint(5.0F, 2.0F + 0.0F, 0.0F);

			arm.rotateAngleX = modelBipedMain.bipedLeftArm.rotateAngleX;
			arm.rotateAngleY = modelBipedMain.bipedLeftArm.rotateAngleY;
			arm.rotateAngleZ = modelBipedMain.bipedLeftArm.rotateAngleZ;

			if (data.doubleEngaged && renderSwingProgress(player, event.partialRenderTick) > 0.0F)
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
			
			try
			{
				Class[] param1 = new Class[]
				{ net.minecraft.entity.Entity.class };
				getEntityTexture = renderer.getClass().getDeclaredMethod("getEntityTexture", param1);
				getEntityTexture.setAccessible(true);
				renderManager.renderEngine.bindTexture((ResourceLocation) getEntityTexture.invoke(event.renderer, new Object[]
				{ player }));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			arm.render(0.0625F);

			modelBipedMain.bipedLeftArm.showModel = false;

			ItemStack stack = data.doubleEngaged ? data.secondItem : null;

			if (stack != null)
			{
				GL11.glPushMatrix();
				arm.postRender(0.0625F);
				GL11.glTranslatef(0.0625F, 0.4375F, 0.0625F);

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
					GL11.glTranslatef(0.1F, 0.1875F, -0.1875F);
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
			}
		}
		else
		{
			modelBipedMain.heldItemLeft = 0;
			modelBipedMain.bipedLeftArm.showModel = true;
		}

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
