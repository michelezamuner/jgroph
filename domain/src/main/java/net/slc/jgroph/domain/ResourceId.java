package net.slc.jgroph.domain;

public class ResourceId
{
    private final int id;

    public ResourceId(final String id)
            throws InvalidResourceIdFormatException
    {
        if (id == null) {
            throw new NullPointerException("ID cannot be null.");
        }

        try {
            this.id = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new InvalidResourceIdFormatException("Invalid resource ID: " + id, e);
        }
    }

    public int toInt()
    {
        return id;
    }

    @Override
    public boolean equals(final Object object)
    {
        if (object == null || !(object instanceof ResourceId)) {
            return false;
        }

        ResourceId resourceId = (ResourceId)object;
        return id == resourceId.id;
    }

    @Override
    public int hashCode()
    {
        return id;
    }
}