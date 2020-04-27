package net.slc.jgroph.adapters.inmemorystorage;

import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.domain.ResourceId;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ResourceRepositoryDataTest
{
    @Test
    public void returnsCorrectStaticData()
    {
        final Map<Integer, String> expected = new HashMap<Integer, String>(){{
            put(1, "Title 1");
            put(2, "Title 2");
        }};

        final ResourceRepositoryData data = new ResourceRepositoryData();

        assertNotEquals(0, data.size());
        for (Map.Entry<ResourceId, ResourceData> entry : data.entrySet()) {
            assertEquals(entry.getKey().toInt(), entry.getValue().getId().toInt());
            assertEquals(expected.get(entry.getKey().toInt()), entry.getValue().getTitle());
        }
    }
}