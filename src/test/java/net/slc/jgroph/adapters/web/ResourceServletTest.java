package net.slc.jgroph.adapters.web;

import com.github.javafaker.Faker;
import net.slc.jgroph.Application;
import net.slc.jgroph.application.*;

import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ResourceServletTest
{
    private Faker faker;

    @Before
    public void setUp()
    {
        this.faker = new Faker();
    }

    @Test
    public void useCaseIsCalledWithCorrectResourceId()
            throws IOException, ServletException, ResourceNotFoundException, InvalidResourceIdFormatException
    {
        final String webResourceId = String.valueOf(this.faker.number().randomNumber());

        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("/" + webResourceId);

        final HttpServletResponse response = mock(HttpServletResponse.class);
        final ResourcePresenter presenter = mock(ResourcePresenter.class);

        final PresenterFactory presenterFactory = mock(PresenterFactory.class);
        when(presenterFactory.createResourcePresenter(response)).thenReturn(presenter);

        final ResourceRepository repository = mock(ResourceRepository.class);
        final ShowResource useCase = mock(ShowResource.class);

        final Application application = mock(Application.class);
        when(application.createResourceRepository()).thenReturn(repository);
        when(application.createShowResource(presenter, repository)).thenReturn(useCase);

        final ResourceServlet servlet = new ResourceServlet(application, presenterFactory);

        servlet.service(request, response);
        verify(useCase).call(eq(webResourceId));
    }

    @Test
    public void useCaseUsesActualResponse()
            throws IOException, ServletException, ResourceNotFoundException, InvalidResourceIdFormatException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("/");

        final HttpServletResponse response = mock(HttpServletResponse.class);
        final ResourcePresenter presenter = mock(ResourcePresenter.class);

        final PresenterFactory presenterFactory = mock(PresenterFactory.class);
        when(presenterFactory.createResourcePresenter(response)).thenReturn(presenter);

        final ResourceRepository repository = mock(ResourceRepository.class);
        final ShowResource useCase = mock(ShowResource.class);

        final Application application = mock(Application.class);
        when(application.createResourceRepository()).thenReturn(repository);
        when(application.createShowResource(presenter, repository)).thenReturn(useCase);

        final ResourceServlet servlet = new ResourceServlet(application, presenterFactory);

        servlet.service(request, response);
        verify(presenterFactory).createResourcePresenter(response);
        verify(application).createShowResource(presenter, repository);
    }

    @Test
    public void errorIsReturnedIfResourceIdHasWrongFormat()
            throws IOException, ServletException, ResourceNotFoundException, InvalidResourceIdFormatException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("/");

        final HttpServletResponse response = mock(HttpServletResponse.class);
        final ErrorPresenter presenter = mock(ErrorPresenter.class);

        final PresenterFactory presenterFactory = mock(PresenterFactory.class);
        when(presenterFactory.createErrorPresenter(response)).thenReturn(presenter);

        final ShowResource useCase = mock(ShowResource.class);
        doThrow(new InvalidResourceIdFormatException("")).when(useCase).call(any());

        final Application application = mock(Application.class);
        when(application.createShowResource(any(), any())).thenReturn(useCase);

        final ResourceServlet servlet = new ResourceServlet(application, presenterFactory);

        servlet.service(request, response);
        verify(presenter).fail(eq(400), any());
    }

    @Test
    public void errorIsReturnedIfResourceIdIsInvalid()
            throws IOException, ServletException, ResourceNotFoundException, InvalidResourceIdFormatException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("/");

        final HttpServletResponse response = mock(HttpServletResponse.class);
        final ErrorPresenter presenter = mock(ErrorPresenter.class);

        final PresenterFactory presenterFactory = mock(PresenterFactory.class);
        when(presenterFactory.createErrorPresenter(response)).thenReturn(presenter);

        final ShowResource useCase = mock(ShowResource.class);
        doThrow(new ResourceNotFoundException("")).when(useCase).call(any());

        final Application application = mock(Application.class);
        when(application.createShowResource(any(), any())).thenReturn(useCase);

        final ResourceServlet servlet = new ResourceServlet(application, presenterFactory);

        servlet.service(request, response);
        verify(presenter).fail(eq(404), any());
    }
}