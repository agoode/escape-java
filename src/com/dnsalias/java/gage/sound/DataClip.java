package com.dnsalias.java.gage.sound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

class DataClip
{
    public byte[] data;
    public int index;
    public AudioFormat format;
    
    public boolean running = false;
    public int sampleRate;
    
    public DataClip(byte[] data, AudioFormat format)
    {
        this.data = data;
        this.index = 0;
        this.format = format;
    }
    
    public DataClip(AudioInputStream in) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
	byte[] data = new byte[in.getFormat().getFrameSize()];
	int length;

	// FIX BY ANTHONY S
	// in.read() only works when the frame size is 1. i.e. 8 bit mono
	// The following code works for any frame size.
	while ((length = in.read(data)) >= 0) out.write(data, 0, length);
        
        in.close();
        
        this.data = out.toByteArray();
        this.index = 0;
        this.format = in.getFormat();
    }
    
    public void calculateSampleRate(int milliseconds)
    {
        sampleRate = (int)((milliseconds*(format.getChannels()*format.getSampleRate()*format.getSampleSizeInBits()/8))/1000);
    }
}
