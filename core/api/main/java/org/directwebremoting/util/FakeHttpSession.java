package org.directwebremoting.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;

/**
 * For the benefit of anyone that wants to create a fake HttpSession
 * that doesn't do anything other than not be null.
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 */
public class FakeHttpSession implements HttpSession
{
    /**
     * Setup the creation time
     */
    public FakeHttpSession()
    {
        creationTime = System.currentTimeMillis();
    }

    /**
     * Setup the creation time
     * @param id The new session id
     */
    public FakeHttpSession(String id)
    {
        this.id = id;
        creationTime = System.currentTimeMillis();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpSession#getCreationTime()
     */
    @Override
    public long getCreationTime()
    {
        return creationTime;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpSession#getId()
     */
    @Override
    public String getId()
    {
        if (id == null)
        {
            log.warn("Inventing data in FakeHttpSession.getId() to remain plausible.");
            id = "fake";
        }

        return id;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpSession#getLastAccessedTime()
     */
    @Override
    public long getLastAccessedTime()
    {
        return creationTime;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpSession#getServletContext()
     */
    @Override
    public ServletContext getServletContext()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpSession#setMaxInactiveInterval(int)
     */
    @Override
    public void setMaxInactiveInterval(int maxInactiveInterval)
    {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpSession#getMaxInactiveInterval()
     */
    @Override
    public int getMaxInactiveInterval()
    {
        return maxInactiveInterval;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpSession#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(String name)
    {
        return attributes.get(name);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpSession#getAttributeNames()
     */
    @Override
    public Enumeration<String> getAttributeNames()
    {
        return Collections.enumeration(attributes.keySet());
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpSession#setAttribute(java.lang.String, java.lang.Object)
     */
    @Override
    public void setAttribute(String name, Object value)
    {
        attributes.put(name, value);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpSession#removeAttribute(java.lang.String)
     */
    @Override
    public void removeAttribute(String name)
    {
        attributes.remove(name);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpSession#invalidate()
     */
    @Override
    public void invalidate()
    {
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpSession#isNew()
     */
    @Override
    public boolean isNew()
    {
        return true;
    }

    /**
     * The session id
     */
    private String id = null;

    /**
     * The list of attributes
     */
    private final Map<String, Object> attributes = new HashMap<String, Object>();

    /**
     * When were we created
     */
    private final long creationTime;

    /**
     * How long before we timeout?
     */
    private int maxInactiveInterval = 30 * 60 * 1000;

    /**
     * The log stream
     */
    private static final Log log = LogFactory.getLog(FakeHttpSession.class);
}
