package mcdelta.ooh.client;

import java.lang.reflect.Field;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class ThirdPersonRenderHandler
{
	private ModelBiped	modelBipedMain;




	@ForgeSubscribe
	public void renderSecondHand (RenderPlayerEvent.Specials.Post event)
	{
		try
		{
			EntityPlayer player = event.entityPlayer;
			RenderPlayer renderer = event.renderer;

			Field fi1 = renderer.getClass().getDeclaredField("modelBipedMain");
			fi1.setAccessible(true);
			modelBipedMain = (ModelBiped) fi1.get(renderer);

			ModelRenderer arm = new ModelRenderer(modelBipedMain, 40, 16);
			arm.mirror = true;
			arm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, 0.0F);
			arm.setRotationPoint(5.0F, 2.0F + 0.0F, 0.0F);

			arm.rotateAngleX = modelBipedMain.bipedLeftArm.rotateAngleX;
			arm.rotateAngleY = modelBipedMain.bipedLeftArm.rotateAngleY;
			arm.rotateAngleZ = modelBipedMain.bipedLeftArm.rotateAngleZ;

			arm.rotateAngleZ = MathHelper.sin(renderSwingProgress(player, event.partialRenderTick) * (float)Math.PI) * -0.4F;
			//arm.rotateAngleX += 100;
			arm.render(0.0625F);

			modelBipedMain.bipedLeftArm.showModel = false;

			// Assets.p(modelBipedMain.onGround);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}




	protected float renderSwingProgress (EntityLivingBase living, float f)
	{
		return living.getSwingProgress(f);
	}
}
