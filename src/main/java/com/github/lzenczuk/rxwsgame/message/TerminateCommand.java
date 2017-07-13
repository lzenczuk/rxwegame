package com.github.lzenczuk.rxwsgame.message;

/**
 * Created by dev on 12/07/17.
 */

public class TerminateCommand implements WSCommand {

    private long clientId;

    public TerminateCommand() {
    }

    public TerminateCommand(long clientId) {
        this.clientId = clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getClientId() {
        return clientId;
    }

    @Override
    public long getMessageId() {
        return 0;
    }

    @Override
    public void applyByteArray(byte[] bytes) {
        System.out.println("Terminate don't need to apply bytes.");
    }

    @Override
    public String toString() {
        return "TerminateCommand{}";
    }
}
