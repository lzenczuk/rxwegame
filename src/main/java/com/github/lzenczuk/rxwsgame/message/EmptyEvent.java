package com.github.lzenczuk.rxwsgame.message;

/**
 * Created by dev on 12/07/17.
 */
public class EmptyEvent implements WSEvent {
    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }
}
