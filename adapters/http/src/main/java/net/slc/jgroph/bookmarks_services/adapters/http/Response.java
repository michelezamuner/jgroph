package net.slc.jgroph.bookmarks_services.adapters.http;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Response
{
    static final int SC_OK = HttpServletResponse.SC_OK;

    private final HttpServletResponse response;

    public Response(final HttpServletResponse response)
    {
        this.response = response;
    }

    void setJsonContentType()
    {
        response.setHeader("Content-Type", "application/json");
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