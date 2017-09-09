package net.slc.jgroph.adapters.web;

import net.slc.jgroph.application.ResourcePresenter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class ResourceServlet extends HttpServlet
{
    private final Factory factory;

    ResourceServlet(final Factory factory)
    {
        this.factory = factory;
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException
    {
        int requestId = Integer.parseInt(request.getPathInfo().substring(1));
        ResourcePresenter presenter = this.factory.createResourcePresenter(response);
        this.factory.createShowResource(presenter).call(requestId);
    }
}