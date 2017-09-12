package net.slc.jgroph.domain;

public class ResourceId
{
    private final int id;

    public ResourceId(final String id)
    {
        this.id = Integer.parseInt(id);
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