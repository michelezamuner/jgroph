package net.slc.jgroph.adapters.web;

import net.slc.jgroph.adapters.App;
import net.slc.jgroph.adapters.AppException;
import net.slc.jgroph.application.InvalidResourceIdFormatException;
import net.slc.jgroph.application.ResourceNotFoundException;
import net.slc.jgroph.application.ShowResource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResourceController
{
    private final App app;

    public ResourceController(final App app)
    {
        this.app = app;
    }

    public void show(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException, AppException
    {
        final String requestId = request.getPathInfo().substring(1);
        try {
            app.make(ShowResource.class).perform(requestId);
        } catch (InvalidResourceIdFormatException e) {
            // TODO: use enumeration for status codes
            app.make(ErrorPresenter.class, response).fail(400, e.getMessage());
        } catch (ResourceNotFoundException e) {
            // TODO: use enumeration for status codes
            app.make(ErrorPresenter.class, response).fail(404, e.getMessage());
        }
    }
}