package org.directwebremoting.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Mike Wilson
 */
public interface FakeHttpServletRequest extends HttpServletRequest
{
    void addUserRole(String role);
    void setContent(byte[] content);
    void setContent(String content);
}

