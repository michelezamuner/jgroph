package net.slc.jgroph.application;

import com.github.javafaker.Faker;
import net.slc.jgroph.domain.InvalidResourceIdFormatException;
import net.slc.jgroph.domain.ResourceId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShowResourceTest
{
    private Faker faker;
    private String id;
    private ResourceId resourceId;
    @Rule public final ExpectedException exception = ExpectedException.none();
    @Mock private ResourcePresenter presenter;
    @Mock private ResourceRepository repository;
    @InjectMocks private ShowResource useCase;

    @Before
    public void setUp()
            throws InvalidResourceIdFormatException
    {
        faker = new Faker();
        id = String.valueOf(faker.number().randomNumber());
        resourceId = new ResourceId(id);
    }

    @Test
    public void presenterIsCalledWithProperData()
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException
    {
        final ResourceData resourceData = new ResourceData(resourceId, this.faker.book().title());
        when(repository.get(resourceId)).thenReturn(resourceData);
        useCase.perform(id);
        verify(presenter).show(eq(resourceData));
    }

    @Test
    public void errorIsThrownIfInvalidIdFormatIsUsed()
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException
    {
        id = "invalid-id";
        exception.expect(InvalidResourceIdFormatException.class);
        exception.expectMessage("Invalid resource ID: " + id);
        useCase.perform(id);
    }

    @Test
    public void errorIsThrownIfInvalidResourceIsRequested()
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException
    {
        final String message = "error message";
        exception.expect(ResourceNotFoundException.class);
        exception.expectMessage(message);
        when(repository.get(eq(resourceId))).thenThrow(new ResourceNotFoundException(message));
        useCase.perform(id);
    }
}