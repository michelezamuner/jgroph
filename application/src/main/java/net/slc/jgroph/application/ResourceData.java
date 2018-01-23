package net.slc.jgroph.application;

import net.slc.jgroph.domain.ResourceId;

public class ResourceData
{
    private final ResourceId id;
    private final String title;

    public ResourceData(final ResourceId id, final String title)
    {
        this.id = id;
        this.title = title;
    }

    public ResourceId getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    @Override
    public boolean equals(final Object object)
    {
        if (!(object instanceof ResourceData)) {
            return false;
        }

        ResourceData resourceData = (ResourceData)object;
        return id.equals(resourceData.id) && title.equals(resourceData.title);
    }

    @Override
    public int hashCode()
    {
        // Hash base: 2. Hash mixer: 37
        return 37 * (74 + id.hashCode()) + title.hashCode();
    }
}