package net.slc.jgroph;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FrontServlet extends HttpServlet
{
    // Class type: 000 (Servlet). Class index: 000
    private static final long serialVersionUID = 0x000_000L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException
    {
        response.getOutputStream().println("Hello, World!");
    }
}