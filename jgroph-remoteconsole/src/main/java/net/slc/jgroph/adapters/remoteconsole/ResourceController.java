package net.slc.jgroph.adapters.remoteconsole;

import net.slc.jgroph.adapters.remoteconsole.router.BadRequestException;
import net.slc.jgroph.adapters.remoteconsole.router.NotFoundException;
import net.slc.jgroph.infrastructure.container.Container;
import net.slc.jgroph.application.ShowResource;
import net.slc.jgroph.application.ResourceNotFoundException;
import net.slc.jgroph.domain.InvalidResourceIdFormatException;

import java.io.IOException;

public class ResourceController
{
    private final Container container;

    public ResourceController(final Container container)
    {
        this.container = container;
    }

    public void show(final String id)
            throws IOException, BadRequestException, NotFoundException
    {
        try {
            container.make(ShowResource.class).perform(id);
        } catch (InvalidResourceIdFormatException e) {
            throw new BadRequestException(e.getMessage(), e);
        } catch (ResourceNotFoundException e) {
            throw new NotFoundException(e.getMessage(), e);
        }
    }
}