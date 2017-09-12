package net.slc.jgroph.adapters.web;

import net.slc.jgroph.Application;
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
    private final Application application;
    private final PresenterFactory presenterFactory;

    ResourceServlet(final Application application, final PresenterFactory presenterFactory)
    {
        this.application = application;
        this.presenterFactory = presenterFactory;
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        final String requestId = request.getPathInfo().substring(1);
        final ResourcePresenter presenter = this.presenterFactory.createResourcePresenter(response);

        try {
            this.application
                    .createShowResource(presenter, this.application.createResourceRepository())
                    .call(requestId);
        } catch (InvalidResourceIdFormatException e) {
            // TODO: use enumeration for status codes
            this.presenterFactory
                    .createErrorPresenter(response)
                    .fail(400, "Invalid resource id format: " + requestId);
        } catch (ResourceNotFoundException e) {
            // TODO: use enumeration for status codes
            this.presenterFactory
                    .createErrorPresenter(response)
                    .fail(404, "Resource " + requestId + " not found.");
        }
    }
}