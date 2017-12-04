package net.slc.jgroph.adapters.remoteconsole;

import com.github.javafaker.Faker;
import net.slc.jgroph.adapters.remoteconsole.router.BadRequestException;
import net.slc.jgroph.adapters.remoteconsole.router.NotFoundException;
import net.slc.jgroph.infrastructure.container.Container;
import net.slc.jgroph.application.ShowResource;
import net.slc.jgroph.domain.InvalidResourceIdFormatException;
import net.slc.jgroph.application.ResourceNotFoundException;

import org.junit.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ResourceControllerTest
{
    private final Faker faker = new Faker();

    @Test
    public void useCaseIsCalledWithCorrectResourceId()
            throws IOException, InvalidResourceIdFormatException, ResourceNotFoundException
    {
        final String id = String.valueOf(faker.number().randomNumber());
        final ShowResource useCase = mock(ShowResource.class);

        final Container container = mock(Container.class);
        when(container.make(ShowResource.class)).thenReturn(useCase);

        final ResourceController controller = new ResourceController(container);
        try {
            controller.show(id);
        } catch (Exception e) {
            // Test fails if action cannot be executed
        }

        verify(useCase).perform(id);
    }

    @Test(expected = BadRequestException.class)
    public void throwsBadRequestIfResourceIdHasWrongFormat()
            throws IOException, BadRequestException, NotFoundException
    {
        final ShowResource useCase = mock(ShowResource.class);
        try {
            doThrow(new InvalidResourceIdFormatException("")).when(useCase).perform(anyString());
        } catch (Exception e) {
            // Test fails if use case cannot throw exception
        }

        final Container container = mock(Container.class);
        when(container.make(ShowResource.class)).thenReturn(useCase);

        final ResourceController controller = new ResourceController(container);
        controller.show("invalid resource id");
    }

    @Test(expected = NotFoundException.class)
    public void throwsNotFoundIfResourceIdIsInvalid()
            throws IOException, BadRequestException, NotFoundException
    {
        final ShowResource useCase = mock(ShowResource.class);
        try {
            doThrow(new ResourceNotFoundException("")).when(useCase).perform(anyString());
        } catch (Exception e) {
            // Test fails if use case cannot throw exception
        }

        final Container container = mock(Container.class);
        when(container.make(ShowResource.class)).thenReturn(useCase);

        final ResourceController controller = new ResourceController(container);
        controller.show("1");
    }
}