package net.slc.jgroph.application;

import com.github.javafaker.Faker;
import net.slc.jgroph.domain.InvalidResourceIdFormatException;
import net.slc.jgroph.domain.ResourceId;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResourceDataTest
{
    private Faker faker;

    @Before
    public void setUp()
    {
        this.faker = new Faker();
    }

    @Test
    public void properlyStoreValues()
            throws InvalidResourceIdFormatException
    {
        final String id = String.valueOf(this.faker.number().randomNumber());
        final ResourceId resourceId = new ResourceId(id);
        final String title = this.faker.book().title();

        final ResourceData resourceData = new ResourceData(resourceId, title);

        assertEquals(resourceId, resourceData.getId());
        assertEquals(title, resourceData.getTitle());
    }

    @Test
    public void canBeProperlyCompared()
            throws InvalidResourceIdFormatException
    {
        final String id = String.valueOf(this.faker.number().randomNumber());
        final ResourceId resourceId = new ResourceId(id);
        final String title = this.faker.book().title();

        final ResourceData first = new ResourceData(resourceId, title);
        final ResourceData second = new ResourceData(resourceId, title);
        final String other = "";
        final ResourceData otherNull = null;

        assertTrue(first.equals(second));
        assertFalse(first.equals(other));
        assertFalse(first.equals(otherNull));

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test(expected = NullPointerException.class)
    public void resourceIdCannotBeNull()
    {
        new ResourceData(null, faker.book().title());
    }

    @Test(expected = NullPointerException.class)
    public void titleCannotBeNull()
            throws InvalidResourceIdFormatException
    {
        new ResourceData(new ResourceId(String.valueOf(faker.number().randomNumber())), null);
    }
}