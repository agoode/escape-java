package org.spacebar.escape.common;

public class UnknownBotException extends IllegalArgumentException {
    private byte type;

    public UnknownBotException(byte type) {
        this.type = type;
    }
    
    public byte getType() {
        return type;
    }
}
