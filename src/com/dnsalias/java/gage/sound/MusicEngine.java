package com.dnsalias.java.gage.sound;

import java.io.IOException;
import java.net.URL;

import javax.sound.midi.*;

/**
 * This is an engine for playing music samples. Currently it only supports MIDI files.
 */

public class MusicEngine
{
    private static Sequence[] sequences = new Sequence[256];
    private static int counter = 0;
    private static Sequencer sequencer;
    
    /**
     * Loads a music sample and returns an id to identify this sample. Games should generally 
     * use the Class.getResource() method for maximum portability. Only 256 music samples can 
     * be loaded simultaneously and the complete data for each clip is loaded into memory.
     */
    public static int load(URL url) throws IOException, InvalidMidiDataException, MidiUnavailableException
    {
        if(sequencer == null) sequencer = MidiSystem.getSequencer();
        sequences[counter] = MidiSystem.getSequence(url);
        
        counter++;
        
        return counter-1;
    }
    
    /**
     * Begins playback of a music sample. If a sample is already playing, it will be stopped and
     * the new one will begin.
     *
     * @param index The id of the music sample as returned by load().
     */
    public static void play(int index)
    {
        if(sequences[index] == null) return;
        
        try
        {
            if(!sequencer.isOpen()) sequencer.open();
            
            sequencer.setSequence(sequences[index]);
            sequencer.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Stops the playback of the current music sample. If no sample is playing, this method
     * simply returns.
     */
    public static void stop()
    {
        try
        {
            if(sequencer.isRunning()) sequencer.stop();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Returns true if the song is still playing. This method may be used
     * to restart playback to loop a song, or to determine if it's time to
     * start the next piece of music.
     */
    public static boolean isRunning()
    {
        return sequencer.isRunning();
    }
    
    /**
     * Total number of music samples currently loaded.
     *
     * @return total samples loaded
     */
    public static int getCount()
    {
        return counter;
    }
    
    /**
     * Stops playback of any music samples and flushes all loaded samples out of memory.
     */
    public static void reset()
    {
        stop();
        
        try
        {   
            if(sequencer.isOpen()) sequencer.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        sequencer = null;
        counter = 0;
        
        for(int i=0; i<sequences.length; i++) sequences[i] = null;
    }
}
