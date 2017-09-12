package net.slc.jgroph.application;

import net.slc.jgroph.domain.ResourceId;

public interface ResourceRepository
{
    public ResourceData get(final ResourceId id)
            throws ResourceNotFoundException;
}