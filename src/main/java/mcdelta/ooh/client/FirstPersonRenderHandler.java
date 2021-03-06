package mcdelta.ooh.client;

import static mcdelta.ooh.OOH.isClient;
import static net.minecraftforge.client.IItemRenderer.ItemRenderType.FIRST_PERSON_MAP;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import mcdelta.ooh.OOH;
import mcdelta.ooh.OOHData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.Project;

public class FirstPersonRenderHandler
{
	private static final ResourceLocation	RES_ITEM_GLINT	       = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	private static final ResourceLocation	RES_MAP_BACKGROUND	   = new ResourceLocation("textures/map/map_background.png");
	private static final ResourceLocation	RES_UNDERWATER_OVERLAY	= new ResourceLocation("textures/misc/underwater.png");

	private double	                      cameraZoom	           = -1;
	private double	                      cameraYaw;
	private double	                      cameraPitch;
	private float	                      farPlaneDistance;
	private int	                          debugViewDirection;

	private Method	                      getFOVModifier;
	private Method	                      hurtCameraEffect;
	private Method	                      setupViewBobbing;




	@ForgeSubscribe
	public void renderSecondHand (RenderWorldLastEvent event)
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		OOHData data = OOHData.getOOHData(player);

		if (data != null && isClient() && data.doubleEngaged)
		{
			EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;

			try
			{
				if (hurtCameraEffect == null)
				{
					Field fi1 = renderer.getClass().getDeclaredField(OOH.isObfuscated ? "field_78503_V" : "cameraZoom");
					fi1.setAccessible(true);
					cameraZoom = (Double) fi1.get(renderer);

					Field fi2 = renderer.getClass().getDeclaredField(OOH.isObfuscated ? "field_78532_q" : "debugViewDirection");
					fi2.setAccessible(true);
					debugViewDirection = (Integer) fi2.get(renderer);

					Field fi3 = renderer.getClass().getDeclaredField(OOH.isObfuscated ? "field_78502_W" : "cameraYaw");
					fi3.setAccessible(true);
					cameraYaw = (Double) fi3.get(renderer);

					Field fi4 = renderer.getClass().getDeclaredField(OOH.isObfuscated ? "field_78509_X" : "cameraPitch");
					fi4.setAccessible(true);
					cameraPitch = (Double) fi4.get(renderer);

					Field fi5 = renderer.getClass().getDeclaredField(OOH.isObfuscated ? "field_78530_s" : "farPlaneDistance");
					fi5.setAccessible(true);
					farPlaneDistance = (Float) fi5.get(renderer);

					Class[] param1 = new Class[]
					{ Float.TYPE, Boolean.TYPE };
					getFOVModifier = renderer.getClass().getDeclaredMethod(OOH.isObfuscated ? "func_78481_a" : "getFOVModifier", param1);
					getFOVModifier.setAccessible(true);

					Class[] param2 = new Class[]
					{ Float.TYPE };
					hurtCameraEffect = renderer.getClass().getDeclaredMethod(OOH.isObfuscated ? "func_78482_e" : "hurtCameraEffect", param2);
					hurtCameraEffect.setAccessible(true);

					Class[] param3 = new Class[]
					{ Float.TYPE };
					setupViewBobbing = renderer.getClass().getDeclaredMethod(OOH.isObfuscated ? "func_78475_f" : "setupViewBobbing", param2);
					setupViewBobbing.setAccessible(true);

				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (cameraZoom == 1.0D)
			{
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				renderHand(renderer, event.partialTicks);
			}
		}
	}




	private void renderHand (EntityRenderer renderer, float partialTicks)
	{
		if (renderer.debugViewDirection <= 0)
		{
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			float f1 = 0.07F;

			if (Minecraft.getMinecraft().gameSettings.anaglyph)
			{
				GL11.glTranslatef((float) (-(partialTicks * 2 - 1)) * f1, 0.0F, 0.0F);
			}

			if (cameraZoom != 1.0D)
			{
				GL11.glTranslatef((float) cameraYaw, (float) (-cameraPitch), 0.0F);
				GL11.glScaled(cameraZoom, cameraZoom, 1.0D);
			}

			try
			{
				Project.gluPerspective((Float) getFOVModifier.invoke(renderer, new Object[]
				{ partialTicks, false }), (float) Minecraft.getMinecraft().displayWidth / (float) Minecraft.getMinecraft().displayHeight, 0.05F, farPlaneDistance * 2.0F);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (Minecraft.getMinecraft().playerController.enableEverythingIsScrewedUpMode())
			{
				float f2 = 0.6666667F;
				GL11.glScalef(1.0F, f2, 1.0F);
			}

			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();

			if (Minecraft.getMinecraft().gameSettings.anaglyph)
			{
				GL11.glTranslatef((float) (partialTicks * 2 - 1) * 0.1F, 0.0F, 0.0F);
			}

			GL11.glPushMatrix();
			try
			{
				hurtCameraEffect.invoke(renderer, new Object[]
				{ partialTicks });
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (Minecraft.getMinecraft().gameSettings.viewBobbing)
			{
				try
				{
					setupViewBobbing.invoke(renderer, new Object[]
					{ partialTicks });
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !Minecraft.getMinecraft().renderViewEntity.isPlayerSleeping() && !Minecraft.getMinecraft().gameSettings.hideGUI && !Minecraft.getMinecraft().playerController.enableEverythingIsScrewedUpMode())
			{
				renderer.enableLightmap((double) partialTicks);
				try
				{
					renderItemInFirstPerson(renderer.itemRenderer, partialTicks);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				renderer.disableLightmap((double) partialTicks);
			}

			GL11.glPopMatrix();

			if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !Minecraft.getMinecraft().renderViewEntity.isPlayerSleeping())
			{
				renderer.itemRenderer.renderOverlays(partialTicks);
				try
				{
					hurtCameraEffect.invoke(renderer, new Object[]
					{ partialTicks });
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			if (Minecraft.getMinecraft().gameSettings.viewBobbing)
			{
				try
				{
					setupViewBobbing.invoke(renderer, new Object[]
					{ partialTicks });
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}




	private void renderItemInFirstPerson (ItemRenderer renderer, float partialTicks) throws Exception
	{
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		OOHData data = OOHData.getOOHData(player);

		float f1 = data.equipProgress[1] + (data.equipProgress[0] - data.equipProgress[1]) * partialTicks;
		EntityClientPlayerMP entityclientplayermp = Minecraft.getMinecraft().thePlayer;
		float f2 = entityclientplayermp.prevRotationPitch + (entityclientplayermp.rotationPitch - entityclientplayermp.prevRotationPitch) * partialTicks;
		GL11.glPushMatrix();
		GL11.glRotatef(f2, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(entityclientplayermp.prevRotationYaw + (entityclientplayermp.rotationYaw - entityclientplayermp.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
		EntityPlayerSP entityplayersp = (EntityPlayerSP) entityclientplayermp;
		float f3 = entityplayersp.prevRenderArmPitch + (entityplayersp.renderArmPitch - entityplayersp.prevRenderArmPitch) * partialTicks;
		float f4 = entityplayersp.prevRenderArmYaw + (entityplayersp.renderArmYaw - entityplayersp.prevRenderArmYaw) * partialTicks;
		GL11.glRotatef((entityclientplayermp.rotationPitch - f3) * 0.1F, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef((entityclientplayermp.rotationYaw - f4) * 0.1F, 0.0F, 1.0F, 0.0F);
		ItemStack itemstack = data.secondItem;
		float f5 = Minecraft.getMinecraft().theWorld.getLightBrightness(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ));
		f5 = 1.0F;
		int i = Minecraft.getMinecraft().theWorld.getLightBrightnessForSkyBlocks(MathHelper.floor_double(entityclientplayermp.posX), MathHelper.floor_double(entityclientplayermp.posY), MathHelper.floor_double(entityclientplayermp.posZ), 0);
		int j = i % 65536;
		int k = i / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		float f6;
		float f7;
		float f8;

		if (itemstack != null)
		{
			i = Item.itemsList[itemstack.itemID].getColorFromItemStack(itemstack, 0);
			f7 = (float) (i >> 16 & 255) / 255.0F;
			f8 = (float) (i >> 8 & 255) / 255.0F;
			f6 = (float) (i & 255) / 255.0F;
			GL11.glColor4f(f5 * f7, f5 * f8, f5 * f6, 1.0F);
		}
		else
		{
			GL11.glColor4f(f5, f5, f5, 1.0F);
		}

		float f9;
		float f10;
		float f11;
		float f12;
		Render render;
		RenderPlayer renderplayer;

		if (itemstack != null && itemstack.getItem() instanceof ItemMap)
		{
			GL11.glPushMatrix();
			f12 = 0.8F;
			f7 = data.getSwingProgress(partialTicks);
			f8 = MathHelper.sin(f7 * (float) Math.PI);
			f6 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float) Math.PI);
			GL11.glTranslatef(-f6 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f7) * (float) Math.PI * 2.0F) * 0.2F, -f8 * 0.2F);
			f7 = 1.0F - f2 / 45.0F + 0.1F;

			if (f7 < 0.0F)
			{
				f7 = 0.0F;
			}

			if (f7 > 1.0F)
			{
				f7 = 1.0F;
			}

			f7 = -MathHelper.cos(f7 * (float) Math.PI) * 0.5F + 0.5F;
			GL11.glTranslatef(0.0F, 0.0F * f12 - (1.0F - f1) * 1.2F - f7 * 0.5F + 0.04F, -0.9F * f12);
			GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(f7 * -85.0F, 0.0F, 0.0F, 1.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			Minecraft.getMinecraft().getTextureManager().bindTexture(entityclientplayermp.getLocationSkin());

			for (k = 0; k < 2; ++k)
			{
				int l = k * 2 - 1;
				GL11.glPushMatrix();
				GL11.glTranslatef(-0.0F, -0.6F, 1.1F * (float) l);
				GL11.glRotatef((float) (-45 * l), 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(59.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef((float) (-65 * l), 0.0F, 1.0F, 0.0F);
				render = RenderManager.instance.getEntityRenderObject(Minecraft.getMinecraft().thePlayer);
				renderplayer = (RenderPlayer) render;
				f11 = 1.0F;
				GL11.glScalef(f11, f11, f11);
				renderplayer.renderFirstPersonArm(Minecraft.getMinecraft().thePlayer);
				GL11.glPopMatrix();
			}

			f8 = data.getSwingProgress(partialTicks);
			f6 = MathHelper.sin(f8 * f8 * (float) Math.PI);
			f9 = MathHelper.sin(MathHelper.sqrt_float(f8) * (float) Math.PI);
			GL11.glRotatef(-f6 * 20.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-f9 * 20.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-f9 * 80.0F, 1.0F, 0.0F, 0.0F);
			f10 = 0.38F;
			GL11.glScalef(f10, f10, f10);
			GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
			f11 = 0.015625F;
			GL11.glScalef(f11, f11, f11);
			Minecraft.getMinecraft().getTextureManager().bindTexture(RES_MAP_BACKGROUND);
			Tessellator tessellator = Tessellator.instance;
			GL11.glNormal3f(0.0F, 0.0F, -1.0F);
			tessellator.startDrawingQuads();
			byte b0 = 7;
			tessellator.addVertexWithUV((double) (0 - b0), (double) (128 + b0), 0.0D, 0.0D, 1.0D);
			tessellator.addVertexWithUV((double) (128 + b0), (double) (128 + b0), 0.0D, 1.0D, 1.0D);
			tessellator.addVertexWithUV((double) (128 + b0), (double) (0 - b0), 0.0D, 1.0D, 0.0D);
			tessellator.addVertexWithUV((double) (0 - b0), (double) (0 - b0), 0.0D, 0.0D, 0.0D);
			tessellator.draw();

			IItemRenderer custom = MinecraftForgeClient.getItemRenderer(itemstack, FIRST_PERSON_MAP);
			MapData mapdata = ((ItemMap) itemstack.getItem()).getMapData(itemstack, Minecraft.getMinecraft().theWorld);

			if (custom == null)
			{
				if (mapdata != null)
				{
					renderer.mapItemRenderer.renderMap(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().getTextureManager(), mapdata);
				}
			}
			else
			{
				custom.renderItem(FIRST_PERSON_MAP, itemstack, Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().getTextureManager(), mapdata);
			}

			GL11.glPopMatrix();
		}
		else if (itemstack != null)
		{
			GL11.glPushMatrix();
			f12 = 0.8F;

			GL11.glScalef(-1.0F, 1.0F, 1.0F);

			if (entityclientplayermp.getItemInUseCount() > 0)
			{
				EnumAction enumaction = itemstack.getItemUseAction();

				if (enumaction == EnumAction.eat || enumaction == EnumAction.drink)
				{
					f8 = (float) entityclientplayermp.getItemInUseCount() - partialTicks + 1.0F;
					f6 = 1.0F - f8 / (float) itemstack.getMaxItemUseDuration();
					f9 = 1.0F - f6;
					f9 = f9 * f9 * f9;
					f9 = f9 * f9 * f9;
					f9 = f9 * f9 * f9;
					f10 = 1.0F - f9;
					GL11.glTranslatef(0.0F, MathHelper.abs(MathHelper.cos(f8 / 4.0F * (float) Math.PI) * 0.1F) * (float) ((double) f6 > 0.2D ? 1 : 0), 0.0F);
					GL11.glTranslatef(f10 * 0.6F, -f10 * 0.5F, 0.0F);
					GL11.glRotatef(f10 * 90.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(f10 * 10.0F, 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(f10 * 30.0F, 0.0F, 0.0F, 1.0F);
				}
			}
			else
			{
				f7 = data.getSwingProgress(partialTicks);
				f8 = MathHelper.sin(f7 * (float) Math.PI);
				f6 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float) Math.PI);
				GL11.glTranslatef(-f6 * 0.4F, MathHelper.sin(MathHelper.sqrt_float(f7) * (float) Math.PI * 2.0F) * 0.2F, -f8 * 0.2F);
			}

			GL11.glTranslatef(0.7F * f12, -0.65F * f12 - (1.0F - f1) * 0.6F, -0.9F * f12);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			f7 = data.getSwingProgress(partialTicks);
			f8 = MathHelper.sin(f7 * f7 * (float) Math.PI);
			f6 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float) Math.PI);
			GL11.glRotatef(-f8 * 20.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-f6 * 20.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-f6 * 80.0F, 1.0F, 0.0F, 0.0F);
			f9 = 0.4F;
			GL11.glScalef(f9, f9, f9);
			float f13;
			float f14;

			if (entityclientplayermp.getItemInUseCount() > 0)
			{
				EnumAction enumaction1 = itemstack.getItemUseAction();

				if (enumaction1 == EnumAction.block)
				{
					GL11.glTranslatef(-0.5F, 0.2F, 0.0F);
					GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
					GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
				}
				else if (enumaction1 == EnumAction.bow)
				{
					GL11.glRotatef(-18.0F, 0.0F, 0.0F, 1.0F);
					GL11.glRotatef(-12.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(-8.0F, 1.0F, 0.0F, 0.0F);
					GL11.glTranslatef(-0.9F, 0.2F, 0.0F);
					f11 = (float) itemstack.getMaxItemUseDuration() - ((float) entityclientplayermp.getItemInUseCount() - partialTicks + 1.0F);
					f13 = f11 / 20.0F;
					f13 = (f13 * f13 + f13 * 2.0F) / 3.0F;

					if (f13 > 1.0F)
					{
						f13 = 1.0F;
					}

					if (f13 > 0.1F)
					{
						GL11.glTranslatef(0.0F, MathHelper.sin((f11 - 0.1F) * 1.3F) * 0.01F * (f13 - 0.1F), 0.0F);
					}

					GL11.glTranslatef(0.0F, 0.0F, f13 * 0.1F);
					GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
					GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
					GL11.glTranslatef(0.0F, 0.5F, 0.0F);
					f14 = 1.0F + f13 * 0.2F;
					GL11.glScalef(1.0F, 1.0F, f14);
					GL11.glTranslatef(0.0F, -0.5F, 0.0F);
					GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
				}
			}

			if (itemstack.getItem().shouldRotateAroundWhenRendering())
			{
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			}

			GL11.glScalef(1.0F, 1.0F, -1.0F);
			GL11.glRotatef(270, 0, 1, 0);

			if (itemstack.getItem().requiresMultipleRenderPasses())
			{
				renderer.renderItem(entityclientplayermp, itemstack, 0, ItemRenderType.EQUIPPED_FIRST_PERSON);
				for (int x = 1; x < itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); x++)
				{
					int i1 = Item.itemsList[itemstack.itemID].getColorFromItemStack(itemstack, x);
					f11 = (float) (i1 >> 16 & 255) / 255.0F;
					f13 = (float) (i1 >> 8 & 255) / 255.0F;
					f14 = (float) (i1 & 255) / 255.0F;
					GL11.glColor4f(f5 * f11, f5 * f13, f5 * f14, 1.0F);
					renderer.renderItem(entityclientplayermp, itemstack, x, ItemRenderType.EQUIPPED_FIRST_PERSON);
				}
			}
			else
			{
				renderer.renderItem(entityclientplayermp, itemstack, 0, ItemRenderType.EQUIPPED_FIRST_PERSON);
			}

			GL11.glPopMatrix();
		}
		else if (!entityclientplayermp.isInvisible())
		{
			GL11.glPushMatrix();

			GL11.glScalef(-1.0F, 1.0F, 1.0F);

			f12 = 0.8F;
			f7 = data.getSwingProgress(partialTicks);
			f8 = MathHelper.sin(f7 * (float) Math.PI);
			f6 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float) Math.PI);
			GL11.glTranslatef(-f6 * 0.3F, MathHelper.sin(MathHelper.sqrt_float(f7) * (float) Math.PI * 2.0F) * 0.4F, -f8 * 0.4F);
			GL11.glTranslatef(0.8F * f12, -0.75F * f12 - (1.0F - f1) * 0.6F, -0.9F * f12);
			GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			f7 = data.getSwingProgress(partialTicks);
			f8 = MathHelper.sin(f7 * f7 * (float) Math.PI);
			f6 = MathHelper.sin(MathHelper.sqrt_float(f7) * (float) Math.PI);
			GL11.glRotatef(f6 * 70.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-f8 * 20.0F, 0.0F, 0.0F, 1.0F);
			Minecraft.getMinecraft().getTextureManager().bindTexture(entityclientplayermp.getLocationSkin());
			GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
			GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);

			GL11.glScalef(1.0F, 1.0F, -1.0F);

			GL11.glTranslatef(5.6F, 0.0F, 0.0F);
			render = RenderManager.instance.getEntityRenderObject(Minecraft.getMinecraft().thePlayer);
			renderplayer = (RenderPlayer) render;
			f11 = 1.0F;
			GL11.glScalef(f11, f11, f11);
			renderplayer.renderFirstPersonArm(Minecraft.getMinecraft().thePlayer);
			GL11.glPopMatrix();
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
	}
}
