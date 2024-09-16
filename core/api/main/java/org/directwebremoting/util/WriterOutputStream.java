package org.directwebremoting.util;

import java.io.IOException;
import java.io.Writer;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

/**
 * This is not the evil hack you are looking for.
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 */
public final class WriterOutputStream extends ServletOutputStream
{
    /**
     * ctor using platform default encoding
     * @param writer The stream that we proxy to
     */
    public WriterOutputStream(Writer writer)
    {
        this.writer = writer;
    }

    /**
     * ctor that allows us to specify how strings are created
     * @param writer The stream that we proxy to
     * @param encoding The string encoding of data that we write to the stream
     */
    public WriterOutputStream(Writer writer, String encoding)
    {
        this.writer = writer;
        this.encoding = encoding;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletOutputStream#print(java.lang.String)
     */
    @Override
    public void print(String s) throws IOException
    {
        writer.write(s);
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(byte[])
     */
    @Override
    public void write(byte[] ba) throws IOException
    {
        if (encoding == null)
        {
            writer.write(new String(ba));
        }
        else
        {
            writer.write(new String(ba, encoding));
        }
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    @Override
    public void write(byte[] ba, int off, int len) throws IOException
    {
        if (encoding == null)
        {
            writer.write(new String(ba, off, len));
        }
        else
        {
            writer.write(new String(ba, off, len, encoding));
        }
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public synchronized void write(int bite) throws IOException
    {
        buffer[0] = (byte) bite;
        write(buffer);
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#close()
     */
    @Override
    public void close() throws IOException
    {
        if (writer != null)
        {
            writer.close();
            writer = null;
            encoding = null;
        }
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#flush()
     */
    @Override
    public void flush() throws IOException
    {
        writer.flush();
    }

    /**
     * The destination of all our printing
     */
    private Writer writer;

    /**
     * What string encoding should we use
     */
    private String encoding = null;

    /**
     * Buffer for write(int)
     */
    private final byte[] buffer = new byte[1];

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletOutputStream#isReady()
     */
    @Override
    public boolean isReady()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletOutputStream#setWriteListener(jakarta.servlet.WriteListener)
     */
    @Override
    public void setWriteListener(WriteListener arg0)
    {
        throw new UnsupportedOperationException("Not implemented");
    }
}
