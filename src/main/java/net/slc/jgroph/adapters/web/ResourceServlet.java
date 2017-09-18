package net.slc.jgroph.adapters.web;

import net.slc.jgroph.infrastructure.container.Container;
import net.slc.jgroph.infrastructure.container.ContainerException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import net.slc.jgroph.adapters.inmemorystorage.ResourceRepository;

class ResourceServlet extends HttpServlet
{
    private final Container container;

    public ResourceServlet(final Container container)
    {
        this.container = container;
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        try {
            ResourcePresenter presenter = container.make(ResourcePresenter.class, response);
            container.bind(net.slc.jgroph.application.ResourcePresenter.class, presenter);
            container.bind(ErrorPresenter.class, container.make(ErrorPresenter.class, response));

            ResourceRepositoryData data = container.make(ResourceRepositoryData.class);
            ResourceRepository repository = container.make(ResourceRepository.class, data);
            container.bind(net.slc.jgroph.application.ResourceRepository.class, repository);

            container.make(ResourceController.class).show(request.getPathInfo().substring(1));
        } catch (ContainerException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }
}