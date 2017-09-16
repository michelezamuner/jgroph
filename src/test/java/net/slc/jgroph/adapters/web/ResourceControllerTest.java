package net.slc.jgroph.adapters.web;

import com.github.javafaker.Faker;
import net.slc.jgroph.adapters.App;
import net.slc.jgroph.adapters.AppException;
import net.slc.jgroph.application.*;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException, AppException
    {
        final String id = String.valueOf(this.faker.number().randomNumber());

        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getPathInfo()).thenReturn("/" + id);

        final HttpServletResponse response = mock(HttpServletResponse.class);
        final ShowResource useCase = mock(ShowResource.class);

        final App app = mock(App.class);
        when(app.make(ShowResource.class)).thenReturn(useCase);

        final ResourceController controller = new ResourceController(app);
        controller.show(request, response);
        verify(useCase).perform(eq(id));
    }

    @Test
    public void errorIsReturnedIfResourceIdHasWrongFormat()
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException, AppException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getPathInfo()).thenReturn("/");

        final HttpServletResponse response = mock(HttpServletResponse.class);
        final ErrorPresenter presenter = mock(ErrorPresenter.class);
        final ShowResource useCase = mock(ShowResource.class);
        final String message = faker.lorem().sentence();
        doThrow(new InvalidResourceIdFormatException(message)).when(useCase).perform(any());

        final App app = mock(App.class);
        when(app.make(ErrorPresenter.class, response)).thenReturn(presenter);
        when(app.make(ShowResource.class)).thenReturn(useCase);

        final ResourceController controller = new ResourceController(app);
        controller.show(request, response);
        verify(presenter).fail(eq(400), eq(message));
    }

    @Test
    public void errorIsReturnedIfResourceIdIsInvalid()
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException, AppException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getPathInfo()).thenReturn("/");

        final HttpServletResponse response = mock(HttpServletResponse.class);
        final ErrorPresenter presenter = mock(ErrorPresenter.class);
        final ShowResource useCase = mock(ShowResource.class);
        final String message = faker.lorem().sentence();
        doThrow(new ResourceNotFoundException(message)).when(useCase).perform(any());

        final App app = mock(App.class);
        when(app.make(ErrorPresenter.class, response)).thenReturn(presenter);
        when(app.make(ShowResource.class)).thenReturn(useCase);

        final ResourceController controller = new ResourceController(app);
        controller.show(request, response);
        verify(presenter).fail(eq(404), eq(message));
    }
}