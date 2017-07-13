package com.github.lzenczuk.rxwsgame.message;

/**
 * Created by dev on 12/07/17.
 */
public class TerminateEvent implements WSEvent {

    private long clientId;

    public TerminateEvent() {
    }

    public TerminateEvent(long clientId) {
        this.clientId = clientId;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }
}
