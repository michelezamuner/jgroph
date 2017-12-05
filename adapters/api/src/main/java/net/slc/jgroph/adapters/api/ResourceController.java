package net.slc.jgroph.adapters.api;

import net.slc.jgroph.infrastructure.container.Container;
import net.slc.jgroph.domain.InvalidResourceIdFormatException;
import net.slc.jgroph.application.ResourceNotFoundException;
import net.slc.jgroph.application.ShowResource;

import java.io.IOException;

public class ResourceController
{
    private final Container container;

    public ResourceController(final Container container)
    {
        this.container = container;
    }

    public void show(final String requestId)
            throws IOException
    {
        try {
            container.make(ShowResource.class).perform(requestId);
        } catch (InvalidResourceIdFormatException e) {
            // TODO: use enumeration for status codes
            container.make(ErrorPresenter.class).fail(400, e.getMessage());
        } catch (ResourceNotFoundException e) {
            // TODO: use enumeration for status codes
            container.make(ErrorPresenter.class).fail(404, e.getMessage());
        }
    }
}