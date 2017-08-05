package com.steven.gateway.handler;

import com.steven.gateway.entity.RedisReply;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by cc on 2017/8/6.
 */
public class RedisReplyEncoder extends MessageToByteEncoder<RedisReply> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RedisReply msg, ByteBuf out) throws Exception {
//        System.out.println("RedisReplyEncoder: " + msg);
        msg.write(out);
    }

}
