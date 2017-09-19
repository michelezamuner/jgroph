package net.slc.jgroph.adapters.web;

import com.github.javafaker.Faker;
import net.slc.jgroph.infrastructure.container.Container;
import net.slc.jgroph.infrastructure.container.ContainerException;
import net.slc.jgroph.application.*;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ResourceControllerTest
{
    private Faker faker;

    @Before
    public void setUp()
    {
        this.faker = new Faker();
    }

    @Test
    public void useCaseIsCalledWithCorrectResourceId()
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException
    {
        final String id = String.valueOf(this.faker.number().randomNumber());
        final ShowResource useCase = mock(ShowResource.class);

        final Container container = mock(Container.class);
        when(container.make(ShowResource.class)).thenReturn(useCase);

        final ResourceController controller = new ResourceController(container);
        controller.show(id);
        verify(useCase).perform(eq(id));
    }

    @Test
    public void errorIsReturnedIfResourceIdHasWrongFormat()
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException
    {
        final ErrorPresenter presenter = mock(ErrorPresenter.class);
        final ShowResource useCase = mock(ShowResource.class);
        final String message = faker.lorem().sentence();
        doThrow(new InvalidResourceIdFormatException(message)).when(useCase).perform(any());

        final Container container = mock(Container.class);
        when(container.make(ErrorPresenter.class)).thenReturn(presenter);
        when(container.make(ShowResource.class)).thenReturn(useCase);

        final ResourceController controller = new ResourceController(container);
        controller.show("/");
        verify(presenter).fail(eq(400), eq(message));
    }

    @Test
    public void errorIsReturnedIfResourceIdIsInvalid()
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException
    {
        final ErrorPresenter presenter = mock(ErrorPresenter.class);
        final ShowResource useCase = mock(ShowResource.class);
        final String message = faker.lorem().sentence();
        doThrow(new ResourceNotFoundException(message)).when(useCase).perform(any());

        final Container container = mock(Container.class);
        when(container.make(ErrorPresenter.class)).thenReturn(presenter);
        when(container.make(ShowResource.class)).thenReturn(useCase);

        final ResourceController controller = new ResourceController(container);
        controller.show("/");
        verify(presenter).fail(eq(404), eq(message));
    }
}