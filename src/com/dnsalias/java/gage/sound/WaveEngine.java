package com.dnsalias.java.gage.sound;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.*;

/**
 * This class is a low-latency sound engine based on the JavaSound API. Once
 * files are loaded and playback is commencing, the render method must be called
 * at regular intervals in order to produce sound. The more often render() is
 * called, the faster render() will return. The sound played through the buffer
 * is based on the time taken between calls to render(). If your framerate drops
 * for whatever reason, you may experience clicks or pops in the sound. It is
 * highly recommended that you use a high-resolution timer with your game in
 * order to properly time this engine. See the <a
 * href="http://java.dnsalias.com">Gage homepage </a> for a free high-res timer.
 * A maximum of 256 clips can be loaded at a time.
 */

public class WaveEngine {
    private static DataClip[] clips = new DataClip[256];

    private static SourceDataLine[] lines = new SourceDataLine[256];

    private static int counter = 0;

    //private static Mixer mixer;
    private static boolean initialized = false;

    private static long last;

    private static void init() {
        //Mixer.Info[] mixers = AudioSystem.getMixerInfo();

        //for(int i=0; i<mixers.length; i++) System.out.println(mixers[i]);

        //mixer = AudioSystem.getMixer(mixers[0]);

        initialized = true;
    }

    /**
     * Loads a digital sample for use as a sound effect. An id to reference that
     * sample is returned. Games should generally use the Class.getResource()
     * method for maximum portability. Only 256 clips can be loaded
     * simultaneously and the complete data for each clip is loaded into memory.
     * 
     * @param url
     *            The URL to the sound sample.
     * @return The id by which the sound can be referenced.
     */
    public static int load(URL url) throws LineUnavailableException,
            IOException, UnsupportedAudioFileException {
        AudioInputStream ain = AudioSystem.getAudioInputStream(url);
        return load(ain);
    }

    public static int load(InputStream in) throws LineUnavailableException, UnsupportedAudioFileException, IOException {
        AudioInputStream ain = AudioSystem.getAudioInputStream(in);
        return load(ain);
    }
    
    private static int load(AudioInputStream ain) throws IOException,
            LineUnavailableException {
        if (!initialized)
            init();

        AudioFormat format = ain.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format,
                AudioSystem.NOT_SPECIFIED);
        DataClip clip = new DataClip(ain);

        clips[counter] = clip;
        lines[counter] = (SourceDataLine) AudioSystem.getLine(info);

        lines[counter].open(format);

        counter++;

        return counter - 1;
    }

    /**
     * Total number of sound samples currently loaded.
     * 
     * @return total samples loaded
     */
    public static int getCount() {
        return counter;
    }

    /**
     * Begins playback of a sample. If the clip is already running, it will be
     * reset and played from the beginning. Playback will continue until the end
     * of the sample is reached.
     * 
     * @param clip
     *            The id of the sample as returned by load().
     */
    public static void play(int clip) {
        if (clips[clip] == null)
            return;

        clips[clip].index = 0;
        clips[clip].running = true;
    }

    /**
     * Stops playback of a sound sample. If the sample is not playing, this
     * method simply returns.
     * 
     * @param clip
     *            The id of the sample as returned by load().
     */
    public static void stop(int clip) {
        if (clips[clip] == null)
            return;

        clips[clip].running = false;
    }

    /**
     * Closes all samples, stops playback, and empties the memory buffer of all
     * clips. In effect, the sound engine is set back to its initial state. This
     * probably should only be called between game levels since there is a heavy
     * cost in time to reset and reload sound samples.
     */
    public static void reset() {
        counter = 0;

        for (int i = 0; i < clips.length; i++) {
            clips[i] = null;

            if (lines[i] != null) {
                lines[i].stop();
                lines[i].close();
                lines[i] = null;
            }
        }
    }

    /**
     * This must be called at regular intervals if sound is to be produced. Try
     * to keep the intervals as consistent as possible to avoid potential buffer
     * underflows which can lead to clicking and popping in the sound stream.
     */
    public static void render() {
        long current = System.currentTimeMillis();
        int difference = (int) (current - last);

        int bytes;

        if (last == 0) {
            last = System.currentTimeMillis();
            return;
        }

        for (int i = 0; i < counter; i++) {
            //Added this line to fix a bug in 1.5. 1.4 stops the line
            //as soon as the buffer underflows. 1.5 does not.
            if (!clips[i].running && lines[i].isRunning())
                lines[i].stop();
            if (!clips[i].running)
                continue;

            clips[i].calculateSampleRate(difference);

            bytes = Math.min(clips[i].sampleRate, clips[i].data.length
                    - clips[i].index);

            // FIX BY ANTHONY S
            // bytes must be a multiple of the frame size. The original code
            // wasn't a problem
            // in the case of 8 bit, mono wavs, but for 16 bit and/or stereo,
            // the frame size
            // is increased. The following line makes sure that bytes is a
            // multiple of
            // the frame size, making WaveEngine compatable with 16 bit stereo
            // wavs.

            bytes -= (bytes % clips[i].format.getFrameSize());

            if (bytes > 0) {
                lines[i].write(clips[i].data, clips[i].index, bytes);
                clips[i].index += bytes;
            }

            if (clips[i].index >= clips[i].data.length)
                clips[i].running = false;

            if (!lines[i].isRunning())
                lines[i].start();
        }

        last = current;
    }
}