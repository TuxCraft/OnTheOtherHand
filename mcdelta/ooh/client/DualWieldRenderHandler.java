package mcdelta.ooh.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import mcdelta.ooh.Assets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public class DualWieldRenderHandler
{
	private double	cameraZoom	= -1;
	private float	cameraYaw;
	private float	farPlaneDistance;
	private int	   cameraPitch;
	private int	   debugViewDirection;

	private Method	getFOVModifier;




	@ForgeSubscribe
	public void renderSecondHand (RenderWorldLastEvent event)
	{
		if (Assets.isClient())
		{
			EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;

			if (getFOVModifier == null)
			{
				try
				{
					Field fi1 = renderer.getClass().getDeclaredField("cameraZoom");
					fi1.setAccessible(true);
					cameraZoom = (Double) fi1.get(renderer);

					Field fi2 = renderer.getClass().getDeclaredField("debugViewDirection");
					fi2.setAccessible(true);
					debugViewDirection = (Integer) fi2.get(renderer);

					Field fi3 = renderer.getClass().getDeclaredField("cameraYaw");
					fi3.setAccessible(true);
					cameraYaw = (Float) fi3.get(renderer);

					Field fi4 = renderer.getClass().getDeclaredField("cameraPitch");
					fi4.setAccessible(true);
					cameraPitch = (Integer) fi4.get(renderer);

					Field fi5 = renderer.getClass().getDeclaredField("farPlaneDistance");
					fi5.setAccessible(true);
					farPlaneDistance = (Float) fi5.get(renderer);

					Class[] param1 = new Class[]
					{ Float.TYPE, Boolean.TYPE };
					getFOVModifier = renderer.getClass().getDeclaredMethod("getFOVModifier", param1);
					getFOVModifier.setAccessible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			if (this.cameraZoom == 1.0D)
			{
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				renderHand(renderer, event.partialTicks);
			}
		}
	}




	private void renderHand (EntityRenderer renderer, float partialTicks)
	{
		if (this.debugViewDirection <= 0)
		{
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			float f1 = 0.07F;

			if (Minecraft.getMinecraft().gameSettings.anaglyph)
			{
				GL11.glTranslatef((float) (-(partialTicks * 2 - 1)) * f1, 0.0F, 0.0F);
			}

			if (this.cameraZoom != 1.0D)
			{
				GL11.glTranslatef((float) this.cameraYaw, (float) (-this.cameraPitch), 0.0F);
				GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0D);
			}

			Project.gluPerspective(getFOVModifier.invoke(renderer, new Object[]
			{ partialTicks, false }), (float) Minecraft.getMinecraft().displayWidth / (float) Minecraft.getMinecraft().displayHeight, 0.05F, this.farPlaneDistance * 2.0F);

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
			this.hurtCameraEffect(partialTicks);

			if (Minecraft.getMinecraft().gameSettings.viewBobbing)
			{
				this.setupViewBobbing(partialTicks);
			}

			if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !Minecraft.getMinecraft().renderViewEntity.isPlayerSleeping() && !Minecraft.getMinecraft().gameSettings.hideGUI && !Minecraft.getMinecraft().playerController.enableEverythingIsScrewedUpMode())
			{
				this.enableLightmap((double) partialTicks);
				this.itemRenderer.renderItemInFirstPerson(partialTicks);
				this.disableLightmap((double) partialTicks);
			}

			GL11.glPopMatrix();

			if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !Minecraft.getMinecraft().renderViewEntity.isPlayerSleeping())
			{
				this.itemRenderer.renderOverlays(partialTicks);
				this.hurtCameraEffect(partialTicks);
			}

			if (Minecraft.getMinecraft().gameSettings.viewBobbing)
			{
				this.setupViewBobbing(partialTicks);
			}
		}
	}
}
