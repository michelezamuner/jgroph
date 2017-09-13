package net.slc.jgroph.adapters.inmemorystorage;

import com.github.javafaker.Faker;
import net.slc.jgroph.application.InvalidResourceIdFormatException;
import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.application.ResourceNotFoundException;
import net.slc.jgroph.domain.ResourceId;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertSame;

public class ResourceRepositoryTest
{
    private Faker faker;

    @Before
    public void setUp()
    {
        this.faker = new Faker();
    }

    @Test
    public void returnsTheCorrectData()
            throws InvalidResourceIdFormatException, ResourceNotFoundException
    {
        final String id = String.valueOf(this.faker.number().randomNumber());
        final ResourceId resourceId = new ResourceId(id);
        final String title = this.faker.book().title();

        final Map<ResourceId, ResourceData> data = new HashMap<ResourceId, ResourceData>(){{
            put(resourceId, new ResourceData(resourceId, title));
        }};

        final ResourceRepository repository = new ResourceRepository(data);

        ResourceData resource = repository.get(resourceId);
        assertSame(resourceId, resource.getId());
        assertSame(title, resource.getTitle());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void throwsErrorIfIdNotValid()
            throws InvalidResourceIdFormatException, ResourceNotFoundException
    {
        final ResourceRepository repository = new ResourceRepository(new HashMap<>());
        repository.get(new ResourceId(String.valueOf(this.faker.number().randomNumber())));
    }
}