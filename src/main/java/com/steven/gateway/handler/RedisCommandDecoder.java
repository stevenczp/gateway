package com.steven.gateway.handler;

import com.steven.gateway.entity.RedisCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * Created by cc on 2017/8/6.
 */
public class RedisCommandDecoder extends ReplayingDecoder<Void> {

    /**
     * Decoded command and arguments
     */
    private byte[][] cmds;

    /**
     * Current argument
     */
    private int arg;

    /**
     * Decode in block-io style, rather than nio.
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (cmds == null) {
            if (in.readByte() == '*') {
                doDecodeNumOfArgs(in);
            }
        } else {
            doDecodeArgs(in);
        }

        if (isComplete()) {
            doSendCmdToHandler(out);
            doCleanUp();
        }
    }

    /**
     * Decode number of arguments
     */
    private void doDecodeNumOfArgs(ByteBuf in) {
        // Ignore negative case
        int numOfArgs = readInt(in);
//        System.out.println("RedisCommandDecoder NumOfArgs: " + numOfArgs);
        cmds = new byte[numOfArgs][];

        checkpoint();
    }

    /**
     * Decode arguments
     */
    private void doDecodeArgs(ByteBuf in) {
        for (int i = arg; i < cmds.length; i++) {
            if (in.readByte() == '$') {
                int lenOfBulkStr = readInt(in);
//                System.out.println("RedisCommandDecoder LenOfBulkStr[" + i + "]: " + lenOfBulkStr);

                cmds[i] = new byte[lenOfBulkStr];
                in.readBytes(cmds[i]);

                // Skip CRLF(\r\n)
                in.skipBytes(2);

                arg++;
                checkpoint();
            } else {
                throw new IllegalStateException("Invalid argument");
            }
        }
    }

    /**
     * cmds != null means header decode complete
     * arg > 0 means arguments decode has begun
     * arg == cmds.length means complete!
     */
    private boolean isComplete() {
        return (cmds != null)
                && (arg > 0)
                && (arg == cmds.length);
    }

    /**
     * Send decoded command to next handler
     */
    private void doSendCmdToHandler(List<Object> out) {
//        System.out.println("RedisCommandDecoder: Send command to next handler");
        if (cmds.length == 2) {
            out.add(new RedisCommand(new String(cmds[0]), cmds[1]));
        } else if (cmds.length == 3) {
            out.add(new RedisCommand(new String(cmds[0]), cmds[1], cmds[2]));
        } else {
            throw new IllegalStateException("Unknown command");
        }
    }

    /**
     * Clean up state info
     */
    private void doCleanUp() {
        this.cmds = null;
        this.arg = 0;
    }

    private int readInt(ByteBuf in) {
        int integer = 0;
        char c;
        while ((c = (char) in.readByte()) != '\r') {
            integer = (integer * 10) + (c - '0');
        }

        if (in.readByte() != '\n') {
            throw new IllegalStateException("Invalid number");
        }
        return integer;
    }

}
