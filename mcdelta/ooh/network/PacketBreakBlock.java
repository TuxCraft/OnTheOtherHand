package mcdelta.ooh.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PacketBreakBlock extends PacketOOH
{

	private boolean	sendOutData;
	private int	    playerID;
	private int	    posX;
	private int	    posY;
	private int	    posZ;




	public PacketBreakBlock ( )
	{
		super(EnumPacketTypes.BREAK_BLOCK);
	}




	public PacketBreakBlock (EntityPlayer player, int x, int y, int z, boolean b)
	{
		super(EnumPacketTypes.BREAK_BLOCK);
		this.playerID = player.entityId;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.sendOutData = b;
	}




	@Override
	public void writeData (DataOutputStream data) throws IOException
	{
		data.writeInt(playerID);
		data.writeInt(posX);
		data.writeInt(posY);
		data.writeInt(posZ);
		data.writeBoolean(sendOutData);
	}




	@Override
	public void readData (DataInputStream data) throws IOException
	{
		playerID = data.readInt();
		posX = data.readInt();
		posY = data.readInt();
		posZ = data.readInt();
		sendOutData = data.readBoolean();
	}




	@Override
	public void execute (INetworkManager manager, Player playerParam)
	{
		EntityPlayer player = (EntityPlayer) playerParam;
		EntityClientPlayerMP thePlayer = (EntityClientPlayerMP) player.worldObj.getEntityByID(playerID);
		World world = player.worldObj;
		Block block = Block.blocksList[world.getBlockId(posX, posY, posZ)];
		int meta = world.getBlockMetadata(posX, posY, posZ);
		
		block.dropBlockAsItem(world, posX, posY, posZ, meta, 0);
	}
}
