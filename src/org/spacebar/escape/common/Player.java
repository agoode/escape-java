package org.spacebar.escape.common;

public class Player extends Entity {
    public Player(int x, int y, byte d) {
        super(x, y, d);

        iAmPlayer();
        iCanTeleport();
        iPushBots();
        iGetHeartFramers();
        
        type = B_PLAYER;
    }
}
