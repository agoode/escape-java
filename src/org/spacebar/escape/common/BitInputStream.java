package org.spacebar.escape.common;

import java.io.*;

public class BitInputStream extends InputStream implements DataInput {
	private byte nextByte;

	private byte bitsLeftInByte;

	private DataInputStream base;
	
	public BitInputStream(InputStream in) {
		super();
		base = new DataInputStream(in);
	}

	public int read() throws IOException {
		return readBits(8);
	}

	public int readBits(int bits) throws IOException {
		int result = 0;
//		System.out.println("readBits: bits " + bits);

		while (bits > 0) {
//			System.out.println("bits left: " + bits);
			result <<= 1;
			try {
				if (readBit()) {
					result++;
				}
			} catch (EOFException e) {
				return -1;
			}
			bits--;
		}
//		System.out.println(": " + Integer.toHexString(result));
		return result;
	}

	private boolean readBit() throws IOException {
		if (bitsLeftInByte == 0) {
//			System.out.println("reading unsignedByte...");
			int result = base.readUnsignedByte();
//			System.out.println("result: " + result);

			nextByte = (byte) result;
			bitsLeftInByte = 8;
		}

		boolean result = ((nextByte & 128) == 128);
		nextByte <<= 1;
		bitsLeftInByte--;

		return result;
	}

	public int readRestOfByte() throws IOException {
		return readBits(bitsLeftInByte);
	}

	public void readFully(byte[] buf) throws IOException {
		readRestOfByte();
		base.readFully(buf);
	}

	public void readFully(byte[] buf, int off, int len) throws IOException {
		readRestOfByte();
		base.readFully(buf, off, len);
	}

	public int skipBytes(int n) throws IOException {
		readRestOfByte();
		return base.skipBytes(n);
	}

	public boolean readBoolean() throws IOException {
		readRestOfByte();
		return base.readBoolean();
	}

	public byte readByte() throws IOException {
		readRestOfByte();
		return base.readByte();
	}

	public int readUnsignedByte() throws IOException {
		readRestOfByte();
		return base.readUnsignedByte();
	}

	public short readShort() throws IOException {
		readRestOfByte();
		return base.readShort();
	}

	public int readUnsignedShort() throws IOException {
		readRestOfByte();
		return base.readUnsignedShort();
	}

	public char readChar() throws IOException {
		readRestOfByte();
		return base.readChar();
	}

	public int readInt() throws IOException {
		readRestOfByte();
		return base.readInt();
	}

	public long readLong() throws IOException {
		readRestOfByte();
		return base.readLong();
	}

	public String readUTF() throws IOException {
		readRestOfByte();
		return base.readUTF(); 
	}
}
