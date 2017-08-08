package com.steven.gateway.entity;

import java.util.Arrays;

/**
 * Created by cc on 2017/8/6.
 */
public class RedisCommand {

    private byte[][] command;

    public RedisCommand(byte[][] command) {
        this.command = command;
    }

    public String getName() {
        return new String(command[0]);
    }

    public byte[] getArg(int index) {
        return command[index + 1];
    }

    @Override
    public String toString() {
        return "RedisCommand{" + "command=" + Arrays.toString(command) + '}';
    }
}
