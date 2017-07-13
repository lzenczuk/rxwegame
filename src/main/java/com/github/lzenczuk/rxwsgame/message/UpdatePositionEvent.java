package com.github.lzenczuk.rxwsgame.message;

import java.nio.ByteBuffer;

/**
 * Created by dev on 12/07/17.
 */
public class UpdatePositionEvent implements WSEvent {

    private final int x;
    private final int y;

    public UpdatePositionEvent(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES * 2);
        buffer.putInt(0, x);
        buffer.putInt(Integer.BYTES, y);
        byte[] array = buffer.array();
        System.out.println("UpdatePositionEvent array: "+array.length);
        return array;
    }
}
