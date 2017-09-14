package net.slc.jgroph.application;

import net.slc.jgroph.domain.ResourceId;

import java.io.IOException;

public class ShowResource
{
    private final ResourcePresenter presenter;
    private final ResourceRepository repository;

    public ShowResource(final ResourcePresenter presenter, final ResourceRepository repository)
    {
        this.presenter = presenter;
        this.repository = repository;
    }

    public void perform(final String resourceId)
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException
    {
        this.presenter.show(this.repository.get(new ResourceId(resourceId)));
    }
}