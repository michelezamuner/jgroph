package net.slc.jgroph.bookmarks_services.adapters.http;

import javax.servlet.http.HttpServletRequest;

public class Request
{
    private final HttpServletRequest request;

    public Request(final HttpServletRequest request)
    {
        this.request = request;
    }

    String getPath()
    {
        return request.getRequestURI();
    }
}