package mcdelta.ooh.client;

import java.lang.reflect.Field;

import mcdelta.ooh.Assets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ForgeSubscribe;

import org.lwjgl.opengl.GL11;

public class DualWieldRenderHandler
{
	private double	cameraZoom	= -1;




	@ForgeSubscribe
	public void renderSecondHand (RenderWorldLastEvent event)
	{
		if (Assets.isClient())
		{
			EntityRenderer renderer = Minecraft.getMinecraft().entityRenderer;

			if (cameraZoom == -1)
			{
				try
				{
					Field fi1 = renderer.getClass().getDeclaredField("cameraZoom");
					fi1.setAccessible(true);
					cameraZoom = (Double) fi1.get(renderer);
				}
				catch (Exception e)
				{

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

	}
}
