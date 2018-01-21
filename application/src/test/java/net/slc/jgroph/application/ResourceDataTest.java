package net.slc.jgroph.application;

import com.github.javafaker.Faker;
import net.slc.jgroph.domain.InvalidResourceIdFormatException;
import net.slc.jgroph.domain.ResourceId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class ResourceDataTest
{
    private ResourceId resourceId;
    private String title;
    @Rule public final ExpectedException exception = ExpectedException.none();

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

    @Test
    public void resourceIdCannotBeNull()
    {
        exception.expect(NullPointerException.class);
        exception.expectMessage("Resource ID cannot be null.");
        new ResourceData(null, title);
    }

    @Test
    public void titleCannotBeNull()
    {
        exception.expect(NullPointerException.class);
        exception.expectMessage("Title cannot be null.");
        new ResourceData(resourceId, null);
    }
}