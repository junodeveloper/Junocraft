package com.skyseasoft.gltest2.server;

/**
 * Created by junodeveloper on 15. 7. 14..
 */
import com.skyseasoft.gltest2.OpenGLRenderer;
import com.skyseasoft.gltest2.SuperChunk;
import com.skyseasoft.gltest2.WorldData;
import com.skyseasoft.gltest2.component.Player;
import com.skyseasoft.gltest2.screen.GLRenderer;
import com.skyseasoft.gltest2.screen.GLResource;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerConnector {
    // Server IP And Port
    public static final String IP = "192.168.25.14";
    public static final int PORT = 9808;

    // Socket, reader, writer
    Socket sock;
    PacketManager wrMan;
    ServerPacketHandler handler;
    ServerPacketCreator creator;
    ArrayList<Player> players = new ArrayList<Player>();
    GLRenderer renderer;
    GLResource res;

    public Player myPlayer;
    SuperChunk spChunk;

    public ServerConnector(OpenGLRenderer renderer) {
        this.renderer = renderer.getRenderer();
        this.res = renderer.getResource();
        this.myPlayer = renderer.getMyPlayer();
        this.spChunk = renderer.getSuperChunk();
    }

    public void run() {
        Thread thread = new SocketHandler();
        thread.start();
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ServerPacketCreator getCreator() {
        return creator;
    }

    public ServerPacketHandler getHandler() {
        return handler;
    }

    public PacketManager getPacketManager() {
        return wrMan;
    }
    class SocketHandler extends Thread {
        public void run() {
            try {
                sock = new Socket(IP, PORT);
                if(sock != null) {
                    wrMan = new PacketManager(new DataInputStream(sock.getInputStream()), new DataOutputStream(sock.getOutputStream()));
                    handler = new ServerPacketHandler(ServerConnector.this);
                    creator = new ServerPacketCreator(ServerConnector.this);
                    while(wrMan.isConnected()) {
                        int header = wrMan.readHeader();
                        handler.handle(header);
                    }
                }
            } catch (Exception e) {
                if(wrMan != null)
                    wrMan.disconnect();
                e.printStackTrace();
            }
        }
    }
}