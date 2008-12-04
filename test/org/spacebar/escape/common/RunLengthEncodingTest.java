package org.spacebar.escape.common;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class RunLengthEncodingTest {
    private static class FakeInputStream extends InputStream {
        private int d = 0;

        @Override
        public int read() throws IOException {
            return d++;
        }
    }

    @Test(expected = IOException.class)
    public void huge() throws IOException {
        BitInputStream in = new BitInputStream(new FakeInputStream());
        RunLengthEncoding.decode(in, Integer.MAX_VALUE);
    }

    @Test(expected = IOException.class)
    public void neg() throws IOException {
        BitInputStream in = new BitInputStream(new FakeInputStream());
        RunLengthEncoding.decode(in, -1);
    }
}
