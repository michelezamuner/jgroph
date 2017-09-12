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

    @Override
    public boolean equals(final Object data)
    {
        if (data == null || !(data instanceof ResourceData)) {
            return false;
        }

        ResourceData resourceData = (ResourceData)data;
        return this.id.equals(resourceData.id) && this.title.equals(resourceData.title);
    }
}