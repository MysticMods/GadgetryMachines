package epicsquid.gadgetry.machines.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class DummyNetHandlerPlayServer extends NetHandlerPlayServer {

  public DummyNetHandlerPlayServer(EntityPlayerMP playerIn) {
    super(null, new NetworkManager(EnumPacketDirection.CLIENTBOUND), playerIn);
  }

  @Override
  public void sendPacket(Packet p) {
  }

  ;

}
