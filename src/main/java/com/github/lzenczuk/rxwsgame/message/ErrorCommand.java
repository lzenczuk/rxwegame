package com.github.lzenczuk.rxwsgame.message;

/**
 * Created by dev on 12/07/17.
 */
public class ErrorCommand implements WSCommand {
    @Override
    public long getMessageId() {
        return -1;
    }

    @Override
    public void applyByteArray(byte[] bytes) {
        // not serializable. it should come from outside
    }
}
