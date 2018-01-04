package net.slc.jgroph.adapters.api;

import com.github.javafaker.Faker;
import net.slc.jgroph.domain.InvalidResourceIdFormatException;
import net.slc.jgroph.infrastructure.container.Container;
import net.slc.jgroph.application.ShowResource;
import net.slc.jgroph.application.ResourceNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyString;

@RunWith(MockitoJUnitRunner.class)
public class ResourceControllerTest
{
    private final Faker faker = new Faker();
    @Mock private ShowResource useCase;
    @Mock private Container container;
    @Mock private ErrorPresenter errorPresenter;
    @InjectMocks private ResourceController controller;

    @Before
    public void setUp()
    {
        when(container.make(ShowResource.class)).thenReturn(useCase);
        when(container.make(ErrorPresenter.class)).thenReturn(errorPresenter);
    }

    @Test
    public void useCaseIsCalledWithCorrectResourceId()
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException
    {
        final String id = String.valueOf(faker.number().randomNumber());
        controller.show(id);
        verify(useCase).perform(id);
    }

    @Test
    public void errorIsReturnedIfResourceIdHasWrongFormat()
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException
    {
        final String message = faker.lorem().sentence();
        doThrow(new InvalidResourceIdFormatException(message)).when(useCase).perform(any());
        controller.show("invalid resource id");
        verify(errorPresenter).fail(eq(400), eq(message));
    }

    @Test
    public void errorIsReturnedIfResourceIdIsInvalid()
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException
    {
        final String message = faker.lorem().sentence();
        doThrow(new ResourceNotFoundException(message)).when(useCase).perform(anyString());
        controller.show("/");
        verify(errorPresenter).fail(eq(404), eq(message));
    }
}