package net.slc.jgroph.adapters.inmemorystorage;

import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.application.ResourceNotFoundException;
import net.slc.jgroph.domain.ResourceId;

import java.util.Map;

public class ResourceRepository implements net.slc.jgroph.application.ResourceRepository
{
    private final Map<ResourceId, ResourceData> data;

    public ResourceRepository(final Map<ResourceId, ResourceData> data)
    {
        this.data = data;
    }

    @Override
    public ResourceData get(final ResourceId id)
            throws ResourceNotFoundException
    {
        if (!data.containsKey(id)) {
            throw new ResourceNotFoundException("Resource " + id.toInt() + " could not be found.");
        }

        return data.get(id);
    }
}