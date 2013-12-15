package mcdelta.ooh.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import mcdelta.ooh.OOHData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;

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
		
		player.attackTargetEntityWithCurrentItem(target);
		
		player.inventory.currentItem = orig;
		
		if (sendOutData)
		{
			PacketDispatcher.sendPacketToAllPlayers(EnumPacketTypes.populatePacket(new PacketAttackEntity(player, target, slot, false)));
		}
	}

}
