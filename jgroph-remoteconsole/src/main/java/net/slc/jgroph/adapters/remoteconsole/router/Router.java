package net.slc.jgroph.adapters.remoteconsole.router;

import net.slc.jgroph.adapters.remoteconsole.ErrorPresenter;
import net.slc.jgroph.adapters.remoteconsole.ResourceController;
import net.slc.jgroph.adapters.remoteconsole.ResourcePresenter;
import net.slc.jgroph.adapters.remoteconsole.router.*;
import net.slc.jgroph.infrastructure.container.Container;

import java.io.IOException;

public class Router
{
    private final Container container;

    public Router(final Container container)
    {
        this.container = container;
    }

    public void route(final Request request, final Response response)
            throws IOException
    {
        container.bind(
                net.slc.jgroph.application.ResourcePresenter.class,
                container.make(ResourcePresenter.class, response)
        );

        try {
            if (request.getMethod().equals(Method.GET) && request.getPrefix().equals("/resources")) {
                container.make(ResourceController.class).show(request.getPath().substring(1));
            }
        } catch (BadRequestException | NotFoundException e) {
            container.make(ErrorPresenter.class, response).show(e.getMessage());
        }
    }
}