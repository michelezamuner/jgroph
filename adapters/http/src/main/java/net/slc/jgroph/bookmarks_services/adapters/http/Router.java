package net.slc.jgroph.bookmarks_services.adapters.http;

import net.slc.jgroph.infrastructure.container.Container;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "router", urlPatterns = {"/"}, loadOnStartup = 1)
public class Router extends HttpServlet
{
    // Class type: 000 (Servlet). Class index: 002
    private static final long serialVersionUID = 0x000_002L;

    private final Container container;

    public Router()
    {
        this(null);
    }

    Router(@Nullable final Container container)
    {
        this.container = container == null ? new Container() : container;
    }

    @Override
    public void init()
    {

    }

    @Override
    protected void doGet(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse)
            throws ServletException, IOException
    {
        final Request request = container.make(Request.class, servletRequest);
        final Response response = container.make(Response.class, servletResponse);

        final String path = request.getPath();
        if (path.equals("/bookmarks")) {
            BookmarksController controller = container.make(BookmarksController.class);
            controller.index(request, response);
            return;
        }

        throw new UnsupportedOperationException("Only /bookmarks is currently supported.");
    }
}