package com.skyseasoft.gltest2.server;

import android.util.Log;

import com.skyseasoft.gltest2.component.Player;

/**
 * Created by junodeveloper on 15. 7. 14..
 */
public class ServerPacketHandler {
    PacketManager wrMan;
    ServerPacketCreator creator;
    ServerConnector conn;
    public ServerPacketHandler(ServerConnector conn) {
        this.conn = conn;
        this.wrMan = conn.getPacketManager();
        creator = new ServerPacketCreator(conn);
    }
    public void handle(int header) {
        switch(header) {
            case ClientRecvs.YOUR_ID:
                int id = wrMan.readInt();
                conn.myPlayer.setId(id);
                break;
            case ClientRecvs.WORLD_MESSAGE: // World Message
                worldMessage();
                break;
            case ClientRecvs.CHANNEL_MESSAGE: // Channle Message
                channelMessage();
                break;
            case ClientRecvs.NOTICE: // Notice
                notice();
                break;
            case ClientRecvs.PLAYER_LIST:
                playerList();
                break;
            case ClientRecvs.NEW_BLOCK:
                newBlock();
                break;
            case ClientRecvs.REMOVE_BLOCK:
                removeBlock();
                break;
            default:
                System.out.println("Unknown Packet. (header : " + header + ")");
                break;
        }
    }
    public void playerList() {
        int users = wrMan.readInt();
        conn.getPlayers().clear();
        for(int i=0;i<users;i++) {
            float x, y, z, xAngle, yAngle, zAngle;
            int id;
            id = wrMan.readInt();
            x = wrMan.readFloat();
            y = wrMan.readFloat();
            z = wrMan.readFloat();
            xAngle = wrMan.readFloat();
            yAngle = wrMan.readFloat();
            zAngle = wrMan.readFloat();
            Player player = new Player(conn.renderer, conn.res);
            player.setId(id);
            player.setAngles(xAngle, yAngle, zAngle);
            player.moveTo(x, y, z);
            conn.getPlayers().add(player);
        }
        Log.d("USERLIST", "There are " + users + " users.");

    }
    public void newBlock() {
        int x = wrMan.readInt();
        int y = wrMan.readInt();
        int z = wrMan.readInt();
        int type = wrMan.readInt();
        conn.spChunk.createBlock(x, y, z, type);
    }
    public void removeBlock() {
        int x = wrMan.readInt();
        int y = wrMan.readInt();
        int z = wrMan.readInt();
        conn.spChunk.removeBlock(x, y, z);
    }
    public void versionDiscord() {
        System.out.println("Version discord occurred.");
        wrMan.disconnect();
    }
    public void string() {
        System.out.println(wrMan.readString());
    }
    public void worldMessage() {
        System.out.println("[WORLD" + wrMan.read() + "] " + wrMan.readString() + " : " + wrMan.readString());
    }
    public void channelMessage() {
        System.out.println("[CHANNEL" + wrMan.read() + "] " + wrMan.readString() + " : " + wrMan.readString());
    }
    public void notice() {
        System.out.println("[NOTICE] " + wrMan.readString());
    }
}
