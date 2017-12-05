package net.slc.jgroph.adapters.remoteconsole.router;

// TODO: these dependencies external to the current package shouldn't exist
import net.slc.jgroph.adapters.remoteconsole.ErrorPresenter;
import net.slc.jgroph.adapters.remoteconsole.ResourceController;
import net.slc.jgroph.adapters.remoteconsole.ResourcePresenterAdapter;

import net.slc.jgroph.infrastructure.container.Container;
import net.slc.jgroph.application.ResourcePresenter;

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
        container.bind(ResourcePresenter.class, container.make(ResourcePresenterAdapter.class, response));

        try {
            if (request.getMethod() == Method.GET && request.getPrefix().equals("/resources")) {
                // TODO: this shouldn't depend on something outside of this package
                container.make(ResourceController.class).show(request.getPath().substring(1));
            }
        } catch (BadRequestException | NotFoundException e) {
            container.make(ErrorPresenter.class, response).show(e.getMessage());
        }
    }
}