package com.github.lzenczuk.rxwsgame.message;

/**
 * Created by dev on 12/07/17.
 */
public interface WSCommand {
    long getMessageId();
    void applyByteArray(byte[] bytes);
}
