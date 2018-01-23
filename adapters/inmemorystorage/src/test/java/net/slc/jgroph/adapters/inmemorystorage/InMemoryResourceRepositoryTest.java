package net.slc.jgroph.adapters.inmemorystorage;

import com.github.javafaker.Faker;
import net.slc.jgroph.domain.InvalidResourceIdFormatException;
import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.application.ResourceNotFoundException;
import net.slc.jgroph.domain.ResourceId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.hamcrest.CoreMatchers.containsString;

import java.util.HashMap;

import static org.junit.Assert.assertSame;

@SuppressWarnings("initialization")
public class InMemoryResourceRepositoryTest
{
    private Faker faker = new Faker();
    private String id;
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp()
    {
        id = String.valueOf(faker.number().randomNumber());
    }

    @Test
    public void returnsTheCorrectData()
            throws InvalidResourceIdFormatException, ResourceNotFoundException
    {
        final ResourceId resourceId = new ResourceId(id);
        final String title = faker.book().title();

        final InMemoryResourceRepository repository = new InMemoryResourceRepository(
            new HashMap<ResourceId, ResourceData>() {
                { put(resourceId, new ResourceData(resourceId, title)); }
            }
        );

        ResourceData resource = repository.get(new ResourceId(id));
        assertSame(resourceId, resource.getId());
        assertSame(title, resource.getTitle());
    }

    @Test
    public void throwsErrorIfIdNotValid()
            throws InvalidResourceIdFormatException, ResourceNotFoundException
    {
        exception.expect(ResourceNotFoundException.class);
        exception.expectMessage(containsString(id));

        final InMemoryResourceRepository repository = new InMemoryResourceRepository(new HashMap<>());
        repository.get(new ResourceId(id));
    }
}