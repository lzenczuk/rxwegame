package com.github.lzenczuk.rxwsgame.message.move;

import com.github.lzenczuk.rxwsgame.message.WSCommand;

/**
 * Created by dev on 12/07/17.
 */
public class MoveCommand implements WSCommand {

    private MoveType moveType;

    @Override
    public long getMessageId() {
        return 1;
    }

    @Override
    public void applyByteArray(byte[] bytes) {
        if(bytes.length!=2){
            throw new RuntimeException("incorrect number of elements in move byte array");
        }

        switch (bytes[1]){
            case 0: moveType=MoveType.UP;
                break;
            case 1: moveType=MoveType.DOWN;
                break;
            case 2: moveType=MoveType.LEFT;
                break;
            case 3: moveType=MoveType.RIGHT;
                break;
            default:
                throw new RuntimeException("Unknown move type: "+bytes[1]);
        }
    }

    public MoveType getMoveType() {
        return moveType;
    }

    @Override
    public String toString() {
        return "MoveCommand{" +
                "moveType=" + moveType +
                '}';
    }
}
