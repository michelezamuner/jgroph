package net.slc.jgroph.application;

import com.github.javafaker.Faker;
import net.slc.jgroph.domain.InvalidResourceIdFormatException;
import net.slc.jgroph.domain.ResourceId;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("initialization")
public class ResourceDataTest
{
    private ResourceId resourceId;
    private String title;

    @Before
    public void setUp()
            throws InvalidResourceIdFormatException
    {
        final Faker faker = new Faker();
        resourceId = new ResourceId(String.valueOf(faker.number().randomNumber()));
        title = faker.book().title();
    }

    @Test
    public void properlyStoreValues()
    {
        final ResourceData resourceData = new ResourceData(resourceId, title);

        assertEquals(resourceId, resourceData.getId());
        assertEquals(title, resourceData.getTitle());
    }

    @Test
    public void canBeProperlyCompared()
    {
        final ResourceData first = new ResourceData(resourceId, title);
        final ResourceData second = new ResourceData(resourceId, title);
        final String other = "";
        final ResourceData otherNull = null;

        assertTrue(first.equals(second));
        assertFalse(first.equals(other));
        assertFalse(first.equals(otherNull));

        assertEquals(first.hashCode(), second.hashCode());
    }
}