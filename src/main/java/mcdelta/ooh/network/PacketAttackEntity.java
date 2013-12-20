package mcdelta.ooh.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketAttackEntity extends PacketOOH
{

	private int	    playerID;
	private int	    targetID;
	private int	    slot;
	private boolean	sendOutData;




	public PacketAttackEntity ( )
	{
		super(EnumPacketTypes.ATTACK_ENTITY);
	}




	public PacketAttackEntity (EntityPlayer player, Entity entityHit, int i, boolean b)
	{
		super(EnumPacketTypes.ATTACK_ENTITY);
		this.playerID = player.entityId;
		this.targetID = entityHit.entityId;
		this.slot = i;
		this.sendOutData = b;
	}




	@Override
	public void writeData (DataOutputStream data) throws IOException
	{
		data.writeInt(playerID);
		data.writeInt(targetID);
		data.writeInt(slot);
		data.writeBoolean(sendOutData);
	}




	@Override
	public void readData (DataInputStream data) throws IOException
	{
		playerID = data.readInt();
		targetID = data.readInt();
		slot = data.readInt();
		sendOutData = data.readBoolean();
	}




	@Override
	public void execute (INetworkManager manager, Player playerParam)
	{
		EntityPlayer e = (EntityPlayer) playerParam;
		EntityPlayer player = (EntityPlayer) e.worldObj.getEntityByID(playerID);
		Entity target = e.worldObj.getEntityByID(targetID);

		int orig = player.inventory.currentItem;
		player.inventory.currentItem = slot;
		
		if (MinecraftForge.EVENT_BUS.post(new AttackEntityEvent(player, target)))
        {
            return;
        }
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null && stack.getItem().onLeftClickEntity(stack, player, target))
        {
            return;
        }
        if (target.canAttackWithItem())
        {
            if (!target.hitByEntity(player))
            {
                float f = (float) (1.0F + ((AttributeModifier) stack.getAttributeModifiers().get(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName()).toArray()[0]).getAmount());
                int i = 0;
                float f1 = 0.0F;

                if (target instanceof EntityLivingBase)
                {
                    f1 = EnchantmentHelper.getEnchantmentModifierLiving(player, (EntityLivingBase)target);
                    i += EnchantmentHelper.getKnockbackModifier(player, (EntityLivingBase)target);
                }

                if (player.isSprinting())
                {
                    ++i;
                }

                if (f > 0.0F || f1 > 0.0F)
                {
                    boolean flag = player.fallDistance > 0.0F && !player.onGround && !player.isOnLadder() && !player.isInWater() && !player.isPotionActive(Potion.blindness) && player.ridingEntity == null && target instanceof EntityLivingBase;

                    if (flag && f > 0.0F)
                    {
                        f *= 1.5F;
                    }

                    f += f1;
                    boolean flag1 = false;
                    int j = EnchantmentHelper.getFireAspectModifier(player);

                    if (target instanceof EntityLivingBase && j > 0 && !target.isBurning())
                    {
                        flag1 = true;
                        target.setFire(1);
                    }

                    boolean flag2 = target.attackEntityFrom(DamageSource.causePlayerDamage(player), f);

                    if (flag2)
                    {
                        if (i > 0)
                        {
                            target.addVelocity((double)(-MathHelper.sin(player.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(player.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F));
                            player.motionX *= 0.6D;
                            player.motionZ *= 0.6D;
                            player.setSprinting(false);
                        }

                        if (flag)
                        {
                            player.onCriticalHit(target);
                        }

                        if (f1 > 0.0F)
                        {
                            player.onEnchantmentCritical(target);
                        }

                        if (f >= 18.0F)
                        {
                            player.triggerAchievement(AchievementList.overkill);
                        }

                        player.setLastAttacker(target);

                        if (target instanceof EntityLivingBase)
                        {
                            EnchantmentThorns.func_92096_a(player, (EntityLivingBase)target, new Random());
                        }
                    }

                    ItemStack itemstack = player.getCurrentEquippedItem();
                    Object object = target;

                    if (target instanceof EntityDragonPart)
                    {
                        IEntityMultiPart ientitymultipart = ((EntityDragonPart)target).entityDragonObj;

                        if (ientitymultipart != null && ientitymultipart instanceof EntityLivingBase)
                        {
                            object = (EntityLivingBase)ientitymultipart;
                        }
                    }

                    if (itemstack != null && object instanceof EntityLivingBase)
                    {
                        itemstack.hitEntity((EntityLivingBase)object, player);

                        if (itemstack.stackSize <= 0)
                        {
                            player.destroyCurrentEquippedItem();
                        }
                    }

                    if (target instanceof EntityLivingBase)
                    {
                        player.addStat(StatList.damageDealtStat, Math.round(f * 10.0F));

                        if (j > 0 && flag2)
                        {
                            target.setFire(j * 4);
                        }
                        else if (flag1)
                        {
                            target.extinguish();
                        }
                    }

                    player.addExhaustion(0.3F);
                }
            }
        }
		
		player.inventory.currentItem = orig;
		
		if (sendOutData)
		{
			PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketAttackEntity(player, target, slot, false)));
		}
	}

}
