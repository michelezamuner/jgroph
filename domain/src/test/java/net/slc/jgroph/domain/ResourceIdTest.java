package net.slc.jgroph.domain;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResourceIdTest
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
        final int id = (int)this.faker.number().randomNumber();
        final ResourceId resourceId = new ResourceId(String.valueOf(id));

        assertEquals(id, resourceId.toInt());
    }

    @Test(expected = InvalidResourceIdFormatException.class)
    public void throwsErrorIfInvalidIdFormat()
            throws InvalidResourceIdFormatException
    {
        final String id = this.faker.book().title();
        new ResourceId(id);
    }

    @Test
    public void canBeProperlyCompared()
            throws InvalidResourceIdFormatException
    {
        final String id = String.valueOf(this.faker.number().randomNumber());

        final ResourceId first = new ResourceId(id);
        final ResourceId second = new ResourceId(id);
        final String other = "";
        final ResourceId otherNull = null;

        assertTrue(first.equals(second));
        assertFalse(first.equals(other));
        assertFalse(first.equals(otherNull));

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test(expected = NullPointerException.class)
    public void idCannotBeNull()
            throws InvalidResourceIdFormatException
    {
        new ResourceId(null);
    }
}