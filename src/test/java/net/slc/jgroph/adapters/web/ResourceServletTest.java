package net.slc.jgroph.adapters.web;

import com.github.javafaker.Faker;
import net.slc.jgroph.application.InvalidResourceIdFormatException;
import net.slc.jgroph.application.ResourceNotFoundException;
import net.slc.jgroph.application.ShowResource;
import net.slc.jgroph.application.ResourcePresenter;

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
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");

        final ShowResource useCase = mock(ShowResource.class);

        final Factory factory = mock(Factory.class);
        when(factory.createShowResource(any())).thenReturn(useCase);

        final ResourceServlet servlet = new ResourceServlet(factory);

        final String webResourceId = String.valueOf(this.faker.number().randomNumber());
        when(request.getPathInfo()).thenReturn("/" + webResourceId);
        servlet.service(request, mock(HttpServletResponse.class));
        verify(useCase).call(eq(webResourceId));;
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
        final ShowResource useCase = mock(ShowResource.class);

        final Factory factory = mock(Factory.class);
        when(factory.createResourcePresenter(response)).thenReturn(presenter);
        when(factory.createShowResource(presenter)).thenReturn(useCase);

        final ResourceServlet servlet = new ResourceServlet(factory);

        servlet.service(request, response);
        verify(factory).createResourcePresenter(response);
        verify(factory).createShowResource(presenter);
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

        final ShowResource useCase = mock(ShowResource.class);
        doThrow(new InvalidResourceIdFormatException("")).when(useCase).call(any());

        final Factory factory = mock(Factory.class);
        when(factory.createErrorPresenter(response)).thenReturn(presenter);
        when(factory.createShowResource(any())).thenReturn(useCase);

        final ResourceServlet servlet = new ResourceServlet(factory);

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

        final ShowResource useCase = mock(ShowResource.class);
        doThrow(new ResourceNotFoundException("")).when(useCase).call(any());

        final Factory factory = mock(Factory.class);
        when(factory.createErrorPresenter(response)).thenReturn(presenter);
        when(factory.createShowResource(any())).thenReturn(useCase);

        final ResourceServlet servlet = new ResourceServlet(factory);

        servlet.service(request, response);
        verify(presenter).fail(eq(404), any());
    }
}