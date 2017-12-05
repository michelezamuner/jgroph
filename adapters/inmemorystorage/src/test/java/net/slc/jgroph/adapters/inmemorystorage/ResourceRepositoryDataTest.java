package net.slc.jgroph.adapters.inmemorystorage;

import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.domain.ResourceId;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ResourceRepositoryDataTest
{
    @Test
    public void returnsCorrectStaticData()
    {
        final ResourceRepositoryData data = new ResourceRepositoryData();
        final StringBuilder string = new StringBuilder();
        for (Map.Entry<ResourceId, ResourceData> entry : data.entrySet()) {
            string.append(entry.getKey().toInt());
            string.append(" ");
            string.append(entry.getValue().getTitle());
            string.append("\n");
        }

        final String expected = "1 Title 1\n2 Title 2\n";
        assertEquals(expected, string.toString());
    }
}