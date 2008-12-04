package org.spacebar.escape.common;

import junit.framework.Assert;

import org.junit.Test;

public class PlayerTest {
    @Test
    public void trueCaps() {
        Player p = new Player(0, 0, (byte) 0);

        Assert.assertTrue(p.isPlayer());
        Assert.assertTrue(p.canTeleport());
        Assert.assertTrue(p.canPushBots());
        Assert.assertTrue(p.canGetHeartframers());
    }

    @Test
    public void falseCaps() {
        Player p = new Player(0, 0, (byte) 0);

        Assert.assertFalse(p.crushesPlayer());
        Assert.assertFalse(p.walksIntoBots());
        Assert.assertFalse(p.canPushPlayer());
        Assert.assertFalse(p.isBomb());
        Assert.assertFalse(p.zapsSelf());
    }
}
