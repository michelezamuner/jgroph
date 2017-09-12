package net.slc.jgroph.domain;

import net.slc.jgroph.application.InvalidResourceIdFormatException;

public class ResourceId
{
    private final int id;

    public ResourceId(final String id)
            throws InvalidResourceIdFormatException
    {
        try {
            this.id = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new InvalidResourceIdFormatException(e.getMessage(), e);
        }
    }

    @Override
    public boolean equals(final Object id)
    {
        if (id == null || !(id instanceof ResourceId)) {
            return false;
        }

        ResourceId resourceId = (ResourceId)id;
        return this.id == resourceId.id;
    }
}