package net.slc.jgroph.bookmarks_services.adapters.http;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Response
{
    static final int SC_OK = HttpServletResponse.SC_OK;
    static final String CT_APPLICATION_JSON = "application/json";

    private final HttpServletResponse response;

    public Response(final HttpServletResponse response)
    {
        this.response = response;
    }

    void setContentType(final String contentType)
    {
        response.setHeader("Content-Type", contentType);
    }

    void write(final String content)
            throws ResponseException
    {
        try {
            response.getWriter().print(content);
        } catch (IOException e) {
            throw new ResponseException(e.getMessage(), e);
        }
    }
}