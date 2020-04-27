package net.slc.jgroph.domain;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

@SuppressWarnings("initialization")
public class ResourceIdTest
{
    private int numericId;
    private String id;
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp()
    {
        final Faker faker = new Faker();
        numericId = (int)faker.number().randomNumber();
        id = String.valueOf(numericId);
    }

    @Test
    public void properlyStoreValues()
            throws InvalidResourceIdFormatException
    {
        final ResourceId resourceId = new ResourceId(id);
        assertEquals(numericId, resourceId.toInt());
    }

    @Test
    public void throwsErrorIfInvalidIdFormat()
            throws InvalidResourceIdFormatException
    {
        final String id = "invalid-id";
        exception.expect(InvalidResourceIdFormatException.class);
        exception.expectMessage("Invalid resource ID: " + id);
        new ResourceId(id);
    }

    @Test
    public void canBeProperlyCompared()
            throws InvalidResourceIdFormatException
    {
        final ResourceId first = new ResourceId(id);
        final ResourceId second = new ResourceId(id);
        final String other = "";
        final ResourceId otherNull = null;

        assertTrue(first.equals(second));
        assertFalse(first.equals(other));
        assertFalse(first.equals(otherNull));

        assertEquals(first.hashCode(), second.hashCode());
    }
}