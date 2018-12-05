package com.skyseasoft.gltest2.server;

/**
 * Created by junodeveloper on 15. 7. 14..
 */

public class ServerRecvs {
    static final int TEST_PACKET = 0;
    static final int PONG = 1;
    static final int WHISPER = 4;
    static final int CHANGE_CHANNEL = 5;
    static final int CHANNEL_MESSAGE = 6;
    static final int WORLD_MESSAGE = 7;
    static final int PLAYER_INFO = 8;
    static final int REQUEST_PLAYERS = 9;
    static final int NEW_BLOCK = 10;
    static final int REMOVE_BLOCK = 11;
}