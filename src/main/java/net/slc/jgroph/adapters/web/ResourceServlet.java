package net.slc.jgroph.adapters.web;

import net.slc.jgroph.application.ShowResource;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class ResourceServlet extends HttpServlet
{
    private final ShowResource useCase;

    ResourceServlet(ShowResource useCase)
    {
        this.useCase = useCase;
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException
    {
        int requestId = Integer.parseInt(request.getPathInfo().substring(1));
        this.useCase.call(requestId);
    }
}