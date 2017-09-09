package net.slc.jgroph.adapters.web;

import net.slc.jgroph.application.InvalidResourceIdFormatException;
import net.slc.jgroph.application.ResourceNotFoundException;
import net.slc.jgroph.application.ResourcePresenter;

import javax.servlet.ServletException;
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
            throws ServletException, IOException
    {
        final String requestId = request.getPathInfo().substring(1);
        final ResourcePresenter presenter = this.factory.createResourcePresenter(response);

        try {
            this.factory.createShowResource(presenter).call(requestId);
        } catch (InvalidResourceIdFormatException e) {
            // TODO: use enumeration for status codes
            factory.createErrorPresenter(response).fail(400, "Invalid resource id format: " + requestId);
        } catch (ResourceNotFoundException e) {
            // TODO: use enumeration for status codes
            factory.createErrorPresenter(response).fail(404, "Resource " + requestId + " not found.");
        }
    }
}