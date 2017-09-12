package net.slc.jgroph.domain;

import com.github.javafaker.Faker;
import net.slc.jgroph.application.InvalidResourceIdFormatException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        final int numericId = (int)this.faker.number().randomNumber();
        final String id = String.valueOf(numericId);

        final ResourceId resourceId = new ResourceId(id);

        assertEquals(id, resourceId.toString());
        assertEquals(numericId, resourceId.toInt());
    }

    @Test(expected = InvalidResourceIdFormatException.class)
    public void throwsErrorIfInvalidIdFormat()
            throws InvalidResourceIdFormatException
    {
        final String id = this.faker.book().title();
        new ResourceId(id);
    }

    @Test
    public void properlyImplementsEquals()
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
    }
}