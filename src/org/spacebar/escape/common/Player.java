package org.spacebar.escape.common;

public class Player extends Entity {
    public Player(int x, int y, int d) {
        super(x, y, d);

        iAmPlayer();
        iCanTeleport();
        iPushBots();
    }
}
