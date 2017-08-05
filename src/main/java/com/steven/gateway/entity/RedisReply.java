package com.steven.gateway.entity;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * Created by cc on 2017/8/6.
 */
public interface RedisReply<T> {

    byte[] CRLF = new byte[]{'\r', '\n'};

    T data();

    void write(ByteBuf out) throws IOException;

}
