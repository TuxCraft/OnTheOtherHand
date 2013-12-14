package mcdelta.ooh.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import mcdelta.ooh.OOH;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

public enum EnumPacketTypes
{
	UPDATE_OOHDATA(PacketOOHData.class);

	public Class<? extends PacketOOH>	clazz;




	EnumPacketTypes (Class<? extends PacketOOH> c)
	{
		this.clazz = c;
	}




	public static PacketOOH buildPacket (byte[] data)
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		int selector = bis.read();
		DataInputStream dis = new DataInputStream(bis);

		PacketOOH packet = null;

		try
		{
			packet = values()[selector].clazz.newInstance();
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
		}

		packet.readPopulate(dis);

		return packet;
	}




	public static Packet populatePacket (PacketOOH packet)
	{
		byte[] data = packet.populate();

		Packet250CustomPayload packet250 = new Packet250CustomPayload();
		packet250.channel = OOH.modid;
		packet250.data = data;
		packet250.length = data.length;
		packet250.isChunkDataPacket = false;

		return packet250;
	}
}
