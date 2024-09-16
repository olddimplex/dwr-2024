package org.directwebremoting.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * Used by ExecutionContext to forward results back via javascript.
 * <p>We could like to implement {@link HttpServletResponse}, but there is a bug
 * in WebLogic where it casts to a {@link HttpServletResponseWrapper} so we
 * need to extend that.
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 */
public final class SwallowingHttpServletResponse implements HttpServletResponse
{
    /**
     * Create a new HttpServletResponse that allows you to catch the body
     * @param response The original HttpServletResponse
     * @param sout The place we copy responses to
     * @param characterEncoding The output encoding
     */
    public SwallowingHttpServletResponse(HttpServletResponse response, Writer sout, String characterEncoding)
    {
        pout = new PrintWriter(sout);
        outputStream = new WriterOutputStream(sout, characterEncoding);

        this.characterEncoding = characterEncoding;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponseWrapper#addCookie(jakarta.servlet.http.Cookie)
     */
    @Override
    public void addCookie(Cookie cookie)
    {
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponseWrapper#addDateHeader(java.lang.String, long)
     */
    @Override
    public void addDateHeader(String name, long value)
    {
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponseWrapper#addHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void addHeader(String name, String value)
    {
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponseWrapper#addIntHeader(java.lang.String, int)
     */
    @Override
    public void addIntHeader(String name, int value)
    {
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponseWrapper#containsHeader(java.lang.String)
     */
    @Override
    public boolean containsHeader(String name)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletResponseWrapper#flushBuffer()
     */
    @Override
    public void flushBuffer() throws IOException
    {
        pout.flush();
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletResponseWrapper#getBufferSize()
     */
    @Override
    public int getBufferSize()
    {
        return bufferSize;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletResponseWrapper#getCharacterEncoding()
     */
    @Override
    public String getCharacterEncoding()
    {
        return characterEncoding;
    }

    /**
     * @return The MIME type of the content
     * @see jakarta.servlet.ServletResponse#setContentType(String)
     */
    @Override
    public String getContentType()
    {
        return contentType;
    }

    /**
     * Accessor for any error messages set using {@link #sendError(int)} or
     * {@link #sendError(int, String)}
     * @return The current error message
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletResponseWrapper#getLocale()
     */
    @Override
    public Locale getLocale()
    {
        return locale;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletResponseWrapper#getOutputStream()
     */
    @Override
    public ServletOutputStream getOutputStream()
    {
        return outputStream;
    }

    /**
     * Accessor for the redirect URL set using {@link #sendRedirect(String)}
     * @return The redirect URL
     */
    public String getRedirectedUrl()
    {
        return redirectedUrl;
    }

    /**
     * What HTTP status code should be returned?
     * @return The current http status code
     */
    @Override
    public int getStatus()
    {
        return status;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletResponseWrapper#getWriter()
     */
    @Override
    public PrintWriter getWriter()
    {
        return pout;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletResponseWrapper#isCommitted()
     */
    @Override
    public boolean isCommitted()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletResponseWrapper#reset()
     */
    @Override
    public void reset()
    {
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletResponseWrapper#resetBuffer()
     */
    @Override
   public void resetBuffer()
    {
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponseWrapper#sendError(int)
     */
    @Override
    public void sendError(int newStatus)
    {
        if (committed)
        {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }

        log.warn("Ignoring call to sendError(" + newStatus + ')');

        status = newStatus;
        committed = true;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponseWrapper#sendError(int, java.lang.String)
     */
    @Override
    public void sendError(int newStatus, String newErrorMessage)
    {
        if (committed)
        {
            throw new IllegalStateException("Cannot set error status - response is already committed");
        }

        log.warn("Ignoring call to sendError(" + newStatus + ", " + newErrorMessage + ')');

        status = newStatus;
        errorMessage = newErrorMessage;
        committed = true;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponseWrapper#sendRedirect(java.lang.String)
     */
    @Override
    public void sendRedirect(String location) throws IOException
    {
        if (committed)
        {
            throw new IllegalStateException("Cannot send redirect - response is already committed");
        }

        log.warn("Ignoring call to sendRedirect('" + location + "')'");

        redirectedUrl = location;
        committed = true;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponse#sendRedirect(java.lang.String, int, boolean)
     */
    @Override
    public void sendRedirect(String location, int statusCode, boolean clearBuffer) throws IOException
    {
        if (committed)
        {
            throw new IllegalStateException("Cannot send redirect - response is already committed");
        }

        log.warn("Ignoring call to sendRedirect('" + location + "', " + statusCode + ", " + clearBuffer + ')');

        redirectedUrl = location;
        committed = true;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletResponseWrapper#setBufferSize(int)
     */
    @Override
    public void setBufferSize(int bufferSize)
    {
        this.bufferSize = bufferSize;
    }

    /**
     * @param characterEncoding The new encoding to use for response strings
     * @see jakarta.servlet.ServletResponseWrapper#getCharacterEncoding()
     */
    @Override
    public void setCharacterEncoding(String characterEncoding)
    {
        this.characterEncoding = characterEncoding;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletResponseWrapper#setContentLength(int)
     */
    @Override
    public void setContentLength(int i)
    {
        // The content length of the original document is not likely to be the
        // same as the content length of the new document.
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletResponse#setContentLengthLong(long)
     */
    @Override
    public void setContentLengthLong(long arg0)
    {
        // see this#setContentLength(int)
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletResponseWrapper#setContentType(java.lang.String)
     */
    @Override
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponseWrapper#setDateHeader(java.lang.String, long)
     */
    @Override
    public void setDateHeader(String name, long value)
    {
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponseWrapper#setHeader(java.lang.String, java.lang.String)
     */
    @Override
    public void setHeader(String name, String value)
    {
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponseWrapper#setIntHeader(java.lang.String, int)
     */
    @Override
    public void setIntHeader(String name, int value)
    {
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.ServletResponseWrapper#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponseWrapper#setStatus(int)
     */
    @Override
    public void setStatus(int status)
    {
        this.status = status;
        log.warn("Ignoring call to setStatus(" + status + ')');
    }

    /**
     * !!! Doesn't check if the given paramString is already URL-encoded. !!!<br>
     *
     * @see jakarta.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
     */
    @Override
    public String encodeURL(String paramString)
    {
        try
        {
            return URLEncoder.encode(
                paramString,
                this.characterEncoding != null
                    ? this.characterEncoding
                    : "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * !!! Doesn't check if the given paramString is already URL-encoded. !!!
     *
     * @see jakarta.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
     */
    @Override
    public String encodeRedirectURL(String paramString)
    {
        return encodeURL(paramString);
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponse#getHeader(java.lang.String)
     */
    @Override
    public String getHeader(String paramString)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponse#getHeaders(java.lang.String)
     */
    @Override
    public Collection<String> getHeaders(String paramString)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see jakarta.servlet.http.HttpServletResponse#getHeaderNames()
     */
    @Override
    public Collection<String> getHeaderNames()
    {
        return null;
    }

    /**
     * The ignored buffer size
     */
    private int bufferSize = 0;

    /**
     * The character encoding used
     */
    private String characterEncoding;

    /**
     * Has the response been committed
     */
    private boolean committed = false;

    /**
     * The MIME type of the output body
     */
    private String contentType = null;

    /**
     * The error message sent with a status != HttpServletResponse.SC_OK
     */
    private String errorMessage = null;

    /**
     * Locale setting: defaults to platform default
     */
    private Locale locale = Locale.getDefault();

    /**
     * The forwarding output stream
     */
    private final ServletOutputStream outputStream;

    /**
     * The forwarding output stream
     */
    private final PrintWriter pout;

    /**
     * Where are we to redirect the user to?
     */
    private String redirectedUrl = null;

    /**
     * The HTTP status
     */
    private int status = HttpServletResponse.SC_OK;

    /**
     * The log stream
     */
    private static final Log log = LogFactory.getLog(SwallowingHttpServletResponse.class);
}
