/*
 * Created on Dec 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.spacebar.escape;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

/**
 * @author adam
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class NESEffects implements Effects {

    private final AudioClip broken;

    private final AudioClip electricOff;

    private final AudioClip exit;

    private final AudioClip hole;

    private final AudioClip laser;

    private final AudioClip noStep;

    private final AudioClip pulse;

    private final AudioClip slide;

    private final AudioClip step;

    private final AudioClip swap;

    private final AudioClip transport;

    private final AudioClip zap;

    private final static String resPrefix = "org/spacebar/escape/resources/nes/";

    public NESEffects() {
        ClassLoader c = getClass().getClassLoader();
        URL u;

        u = c.getResource(resPrefix + "broken.wav");
        broken = Applet.newAudioClip(u);

        u = c.getResource(resPrefix + "electric-off.wav");
        electricOff = Applet.newAudioClip(u);

        u = c.getResource(resPrefix + "exit.wav");
        exit = Applet.newAudioClip(u);

        u = c.getResource(resPrefix + "hole.wav");
        hole = Applet.newAudioClip(u);

        u = c.getResource(resPrefix + "laser.wav");
        laser = Applet.newAudioClip(u);

        u = c.getResource(resPrefix + "no-step.wav");
        noStep = Applet.newAudioClip(u);

        u = c.getResource(resPrefix + "pulse.wav");
        pulse = Applet.newAudioClip(u);

        u = c.getResource(resPrefix + "slide.wav");
        slide = Applet.newAudioClip(u);

        u = c.getResource(resPrefix + "step.wav");
        step = Applet.newAudioClip(u);

        u = c.getResource(resPrefix + "swap.wav");
        swap = Applet.newAudioClip(u);

        u = c.getResource(resPrefix + "transport.wav");
        transport = Applet.newAudioClip(u);

        u = c.getResource(resPrefix + "zap.wav");
        zap = Applet.newAudioClip(u);
    }
    
    
    public void doBroken() {
        broken.play();
    }

    public void doElectricOff() {
        electricOff.play();
    }

    public void doExit() {
        exit.play();
    }

    public void doHole() {
        hole.play();
    }

    public void doLaser() {
        laser.play();
    }

    public void doNoStep() {
        noStep.play();
    }

    public void doPulse() {
        pulse.play();
    }

    public void doSlide() {
        slide.play();
    }

    public void doStep() {
        step.play();
    }

    public void doSwap() {
        swap.play();
    }

    public void doTransport() {
        transport.play();
    }

    public void doZap() {
        zap.play();
    }
}