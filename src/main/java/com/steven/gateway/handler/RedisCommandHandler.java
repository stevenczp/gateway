package com.steven.gateway.handler;

import com.steven.gateway.entity.BulkReply;
import com.steven.gateway.entity.IntegerReply;
import com.steven.gateway.entity.RedisCommand;
import com.steven.gateway.entity.SimpleReply;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;

/**
 * Created by cc on 2017/8/6.
 */
@ChannelHandler.Sharable
public class RedisCommandHandler extends SimpleChannelInboundHandler<RedisCommand> {

    private HashMap<String, byte[]> database = new HashMap<String, byte[]>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RedisCommand msg) throws Exception {
        switch (msg.getName().toLowerCase()) {
            case "set":
                database.put(new String(msg.getArg(0)), msg.getArg(1));
                ctx.writeAndFlush(SimpleReply.OK_REPLY);
                break;
            case "get":
                byte[] value = database.get(new String(msg.getArg(0)));
                if (value != null && value.length > 0) {
                    ctx.writeAndFlush(new BulkReply(value));
                } else {
                    ctx.writeAndFlush(BulkReply.NIL_REPLY);
                }
                break;
            case "ping":
                ctx.writeAndFlush(SimpleReply.PONG_REPLY);
            default:
        }
    }

}
