package net.slc.jgroph.adapters.remoteconsole;

import com.github.javafaker.Faker;
import net.slc.jgroph.adapters.remoteconsole.router.BadRequestException;
import net.slc.jgroph.adapters.remoteconsole.router.NotFoundException;
import net.slc.jgroph.infrastructure.container.Container;
import net.slc.jgroph.application.ShowResource;
import net.slc.jgroph.domain.InvalidResourceIdFormatException;
import net.slc.jgroph.application.ResourceNotFoundException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("initialization")
public class ResourceControllerTest
{
    private final Faker faker = new Faker();
    @Rule public final ExpectedException exception = ExpectedException.none();
    @Mock private ShowResource useCase;
    @Mock private Container container;
    @InjectMocks private ResourceController controller;

    @Before
    public void setUp()
    {
        when(container.make(ShowResource.class)).thenReturn(useCase);
    }

    @Test
    public void useCaseIsCalledWithCorrectResourceId()
            throws IOException, InvalidResourceIdFormatException, ResourceNotFoundException
    {
        final String id = String.valueOf(faker.number().randomNumber());
        try {
            controller.show(id);
        } catch (Exception e) {
            // Test fails if the action cannot be executed
        }

        verify(useCase).perform(id);
    }

    @Test
    public void throwsBadRequestIfResourceIdHasWrongFormat()
            throws BadRequestException
    {
        final String error = "Error message";
        exception.expect(BadRequestException.class);
        exception.expectMessage(error);
        try {
            doThrow(new InvalidResourceIdFormatException(error)).when(useCase).perform(anyString());
            controller.show("invalid resource id");
        } catch(BadRequestException e) {
            throw e;
        } catch (Exception e) {
            // Test fails if the use case doesn't throw the right exception
        }
    }

    @Test
    public void throwsNotFoundIfResourceIdIsInvalid()
            throws NotFoundException
    {
        final String error = "Error message";
        exception.expect(NotFoundException.class);
        exception.expectMessage(error);
        try {
            doThrow(new ResourceNotFoundException(error)).when(useCase).perform(anyString());
            controller.show("1");
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            // Test fails if the use case doesn't throw the right exception
        }
    }
}