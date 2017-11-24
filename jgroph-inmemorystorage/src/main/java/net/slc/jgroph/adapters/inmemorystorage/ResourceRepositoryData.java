package net.slc.jgroph.adapters.inmemorystorage;

import net.slc.jgroph.domain.InvalidResourceIdFormatException;
import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.domain.ResourceId;

import java.util.HashMap;

public class ResourceRepositoryData extends HashMap<ResourceId, ResourceData>
{
    // Class type: FFF (Other). Class index: 000
    private static final long serialVersionUID = 0xFFF_000L;

    public ResourceRepositoryData()
    {
        try {
            put(new ResourceId("1"), new ResourceData(new ResourceId("1"), "Title 1"));
            put(new ResourceId("2"), new ResourceData(new ResourceId("2"), "Title 2"));
        } catch (InvalidResourceIdFormatException e) {
            // TODO: The whole class is going to be replaced anyway.
            System.out.println(e.getMessage());
        }
    }
}