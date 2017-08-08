package com.steven.gateway.entity;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public class SimpleReply implements RedisReply<byte[]> {
    public static final SimpleReply OK_REPLY = new SimpleReply("OK");
    public static final SimpleReply PONG_REPLY = new SimpleReply("PONG");

    private static final char MARKER = '+';

    private final byte[] data;

    public SimpleReply(String data) {
        this.data = data.getBytes();
    }

    public SimpleReply(byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] data() {
        return data;
    }

    @Override
    public void write(ByteBuf out) throws IOException {
        out.writeByte(MARKER);
        out.writeBytes(data);
        out.writeBytes(CRLF);
    }
}
