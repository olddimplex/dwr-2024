package org.directwebremoting.server.jetty;

import java.io.IOException;

import org.directwebremoting.extend.RealScriptSession;
import org.directwebremoting.extend.ScriptConduit;
import org.directwebremoting.impl.BaseSleeper;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * A Sleeper that works with Jetty Continuations
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 * @author Tim Peierls [tim at peierls dot net]
 * @author Mike Wilson
 */
public class JettyContinuationSleeper extends BaseSleeper
{
    /**
     * @param request The request into which we store this as an attribute
     * @param conduit
     * @param scriptSession
     * @param response
     * @throws IOException
     */
    public JettyContinuationSleeper(final HttpServletRequest request, final HttpServletResponse response, RealScriptSession scriptSession, ScriptConduit conduit) throws IOException
    {
        super(response, scriptSession, conduit);

        this.request = request;

        workInProgress = true; // block doing work until we officially enter sleep
    }

    /* (non-Javadoc)
     * @see org.directwebremoting.impl.BaseSleeper#enterSleep()
     */
    @Override
    protected void enterSleep()
    {
        asyncContext = request.startAsync();
        request.setAttribute(ATTRIBUTE_SLEEPER, this);
        asyncContext.setTimeout(0);
//        asyncContext.addListener(new AsyncListener() {
//
//            @Override
//            public void onComplete(final AsyncEvent event) throws IOException {}
//
//            @Override
//            public void onError(final AsyncEvent event) throws IOException {
//                event.getThrowable();
//            }
//
//            @Override
//            public void onStartAsync(final AsyncEvent event) throws IOException {}
//
//            @Override
//            public void onTimeout(final AsyncEvent event) throws IOException {}
//        });

        synchronized (workLock)
        {
            if (queuedWork) {
                asyncContext.dispatch(); // will eventually trigger resumeWork() from a container thread
            } else {
                workInProgress = false; // open up for doing new work
            }
            queuedWork = false;
        }
    }

    /* (non-Javadoc)
     * @see org.directwebremoting.impl.BaseSleeper#wakeUp()
     */
    @Override
    protected void wakeUp()
    {
        synchronized (workLock)
        {
            if (workInProgress) {
                queuedWork = true;
            } else {
                asyncContext.dispatch(); // will eventually trigger resumeWork() from a container thread
                workInProgress = true;
            }
        }
    }

    /**
     * The method that should be called by a background thread when we are being awakened
     */
    public void resumeWork()
    {
        while(true) {
            doWork();
            if (request.getAttribute(ATTRIBUTE_SLEEPER) == null) {
                // If we have disconnected the Sleeper from the container then it is time to end the async cycle
                return;
            }
            synchronized (workLock)
            {
                if (queuedWork) {
                    // New work has arrived so fall through to take another spin in the loop
                    queuedWork = false;
                } else {
                    // No work in the queue so exit loop and go to sleep again
                    asyncContext.dispatch();
                    workInProgress = false;
                    return;
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.directwebremoting.impl.BaseSleeper#close()
     */
    @Override
    protected void close()
    {
        if (asyncContext != null) {
            request.removeAttribute(ATTRIBUTE_SLEEPER);
        }
    }

    /**
     * Retrieve a stashed sleeper from a request.
     */
    public static JettyContinuationSleeper getSleeper(HttpServletRequest request)
    {
        return (JettyContinuationSleeper) request.getAttribute(ATTRIBUTE_SLEEPER);
    }

    // Set at construction
    private final HttpServletRequest request;

    // Internal state
    private AsyncContext asyncContext = null;
    private final Object workLock = new Object();
    private boolean workInProgress = false;
    private boolean queuedWork = false;

    /**
     * We remember the sleeper on a request attribute
     */
    protected static final String ATTRIBUTE_SLEEPER = JettyContinuationSleeper.class.getName();
}
