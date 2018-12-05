package com.skyseasoft.gltest2.server;

/**
 * Created by junodeveloper on 15. 7. 14..
 */
import android.util.Log;

import java.io.*;

import java.io.*;

public class PacketManager {
    DataInputStream reader;
    DataOutputStream writer;
    boolean connected;
    public PacketManager(DataInputStream reader, DataOutputStream writer) {
        this.reader = reader;
        this.writer = writer;
        connected = true;
    }
    public void write(int data) {
        try {
            writer.write(data);
            writer.flush();
        } catch (IOException e) { e.printStackTrace(); disconnect(); }
    }
    public void writeShort(int data) {
        try {
            writer.writeShort(data);
            writer.flush();
        } catch (IOException e) { e.printStackTrace(); disconnect(); }
    }
    public void writeInt(int data) {
        try {
            writer.writeInt(data);
            writer.flush();
        } catch (IOException e) { e.printStackTrace(); disconnect(); }
    }
    public void writeLong(long data) {
        try {
            writer.writeLong(data);
            writer.flush();
        } catch (IOException e) { e.printStackTrace(); disconnect(); }
    }
    public void writeFloat(float data) {
        try {
            writer.writeFloat(data);
            writer.flush();
        } catch (IOException e) { e.printStackTrace(); disconnect(); }
    }

    public void writeDouble(double data) {
        try {
            writer.writeDouble(data);
            writer.flush();
        } catch (IOException e) { e.printStackTrace(); disconnect(); }
    }
    public void writeString(String str) {
        try {
            byte[] b = str.getBytes("utf-8");
            writer.writeShort(b.length);
            writer.flush();
            writer.write(b);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }
    public void writeHeader(int header) {
        writeShort(header);
    }
    public int read() {
        int ret = -1;
        try {
            ret = reader.readByte();
        } catch (IOException e) { e.printStackTrace(); disconnect(); }
        return ret;
    }
    public int readShort() {
        int ret = -1;
        try {
            ret = reader.readShort();
        } catch (IOException e) { e.printStackTrace(); disconnect(); }
        return ret;
    }
    public int readInt() {
        int ret = -1;
        try {
            ret = reader.readInt();
        } catch (IOException e) { e.printStackTrace(); disconnect(); }
        return ret;
    }
    public long readLong() {
        long ret = -1;
        try {
            ret = reader.readLong();
        } catch (IOException e) { e.printStackTrace(); disconnect(); }
        return ret;
    }
    public float readFloat() {
        float ret = 0.0f;
        try {
            ret = reader.readFloat();
        } catch (IOException e) { e.printStackTrace(); disconnect(); }
        return ret;
    }
    public double readDouble() {
        double ret = 0.0;
        try {
            ret = reader.readDouble();
        } catch (IOException e) { e.printStackTrace(); disconnect(); }
        return ret;
    }
    public String readString() {
        int len = readShort();
        byte[] b = new byte[len];
        String ret = null;
        try {
            reader.read(b);
            ret = new String(b, "utf-8");
        } catch (IOException e) { e.printStackTrace(); disconnect(); }
        return ret;
    }
    public int readHeader() {
        return readShort();
    }
    public boolean isConnected() {
        return connected;
    }
    public void disconnect() {
        try {
            Log.d("SOCKETDISCONNECT", "YES");
            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connected = false;
    }
}