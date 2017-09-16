package net.slc.jgroph.adapters.web;

import net.slc.jgroph.adapters.App;
import net.slc.jgroph.adapters.AppException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import net.slc.jgroph.adapters.inmemorystorage.ResourceRepository;

class ResourceServlet extends HttpServlet
{
    private final App app;

    public ResourceServlet(final App app)
    {
        this.app = app;
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        try {
            app.bind(net.slc.jgroph.application.ResourcePresenter.class, app.make(ResourcePresenter.class, response));
            app.bind(ErrorPresenter.class, app.make(ErrorPresenter.class, response));

            ResourceRepositoryData data = app.make(ResourceRepositoryData.class);
            app.bind(net.slc.jgroph.application.ResourceRepository.class, app.make(ResourceRepository.class, data));

            app.make(ResourceController.class).show(request, response);
        } catch (AppException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }
}