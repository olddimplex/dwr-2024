package org.directwebremoting.util;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

/**
 * Delegating implementation of ServletInputStream.
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 */
public class DelegatingServletInputStream extends ServletInputStream
{
    /**
     * Create a new DelegatingServletInputStream.
     * @param proxy the sourceStream InputStream
     */
    public DelegatingServletInputStream(InputStream proxy)
    {
        this.proxy = proxy;
    }

    /**
     * Accessor for the stream that we are proxying to
     * @return The stream we proxy to
     */
    public InputStream getTargetStream()
    {
        return proxy;
    }

    /**
     * @return The stream that we proxy to
     */
    public InputStream getSourceStream()
    {
        return proxy;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException
    {
        return proxy.read();
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException
    {
        super.close();
        proxy.close();
    }

    private final InputStream proxy;

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletInputStream#isFinished()
     */
    @Override
    public boolean isFinished()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletInputStream#isReady()
     */
    @Override
    public boolean isReady()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletInputStream#setReadListener(jakarta.servlet.ReadListener)
     */
    @Override
    public void setReadListener(ReadListener arg0)
    {
        throw new UnsupportedOperationException("Not implemented");
    }
}
