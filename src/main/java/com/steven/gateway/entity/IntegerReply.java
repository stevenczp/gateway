package com.steven.gateway.entity;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * Created by cc on 2017/8/6.
 */
public class IntegerReply implements RedisReply<Integer> {

    private static final char MARKER = ':';

    private final int data;

    public IntegerReply(int data) {
        this.data = data;
    }

    public Integer data() {
        return this.data;
    }

    public void write(ByteBuf out) throws IOException {
        out.writeByte(MARKER);
        out.writeBytes(String.valueOf(data).getBytes());
        out.writeBytes(CRLF);
    }

    @Override
    public String toString() {
        return "IntegerReply{" +
                "data=" + data +
                '}';
    }

}
