package net.slc.jgroph.application;

import net.slc.jgroph.domain.ResourceId;

public class ResourceData
{
    private final ResourceId id;
    private final String title;

    public ResourceData(final ResourceId id, final String title)
    {
        if (id == null) {
            throw new NullPointerException("Resource ID cannot be null.");
        }

        if (title == null) {
            throw new NullPointerException("Title cannot be null.");
        }

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
        if (object == null || !(object instanceof ResourceData)) {
            return false;
        }

        ResourceData resourceData = (ResourceData)object;
        return id.equals(resourceData.id) && title.equals(resourceData.title);
    }
}