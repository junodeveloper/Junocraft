package com.skyseasoft.gltest2.server;

import com.skyseasoft.gltest2.component.Player;

/**
 * Created by junodeveloper on 15. 7. 14..
 */
public class ServerPacketCreator {
    PacketManager wrMan;
    ServerConnector conn;
    public ServerPacketCreator(ServerConnector conn) {
        this.conn = conn;
        this.wrMan = conn.getPacketManager();
    }
    public void sendWorldMessage(String msg) {
        wrMan.writeHeader(ServerRecvs.WORLD_MESSAGE);
        wrMan.writeString(msg);
    }
    public void sendChannelMessage(String msg) {
        wrMan.writeHeader(ServerRecvs.CHANNEL_MESSAGE);
        wrMan.writeString(msg);
    }
    public void sendPlayerInfo(Player player) {
        wrMan.writeHeader(ServerRecvs.PLAYER_INFO);
        wrMan.writeInt(player.getId());
        wrMan.writeFloat(player.getX());
        wrMan.writeFloat(player.getY());
        wrMan.writeFloat(player.getZ());
        float[] angles = player.getAngles();
        wrMan.writeFloat(angles[0]);
        wrMan.writeFloat(angles[1]);
        wrMan.writeFloat(angles[2]);
    }
    public void sendRequestPlayers() {
        wrMan.writeHeader(ServerRecvs.REQUEST_PLAYERS);
    }
    public void sendNewBlock(int x, int y, int z, int type) {
        wrMan.writeHeader(ServerRecvs.NEW_BLOCK);
        wrMan.writeInt(x);
        wrMan.writeInt(y);
        wrMan.writeInt(z);
        wrMan.writeInt(type);
    }
    public void sendRemoveBlock(int x, int y, int z) {
        wrMan.writeHeader(ServerRecvs.REMOVE_BLOCK);
        wrMan.writeInt(x);
        wrMan.writeInt(y);
        wrMan.writeInt(z);
    }
}