package net.slc.jgroph.adapters.web;

import net.slc.jgroph.application.ShowResource;
import net.slc.jgroph.application.ResourcePresenter;

import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class ResourceServletTest
{
    @Test
    public void useCaseIsCalledWithCorrectResourceId()
            throws IOException, ServletException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("/1");
        final ShowResource useCase = mock(ShowResource.class);
        final ResourcePresenter presenter = mock(ResourcePresenter.class);

        final Factory factory = mock(Factory.class);
        when(factory.createResourcePresenter(any())).thenReturn(presenter);
        when(factory.createShowResource(presenter)).thenReturn(useCase);

        final ResourceServlet servlet = new ResourceServlet(factory);

        when(request.getPathInfo()).thenReturn("/1");
        servlet.service(request, mock(HttpServletResponse.class));
        verify(useCase).call(1);

        when(request.getPathInfo()).thenReturn("/1234");
        servlet.service(request, mock(HttpServletResponse.class));
        verify(useCase).call(1234);
    }

    @Test
    public void useCaseUsesActualResponse()
            throws IOException, ServletException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("/1");
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final ResourcePresenter presenter = mock(ResourcePresenter.class);
        final ShowResource useCase = mock(ShowResource.class);
        final Factory factory = mock(Factory.class);

        final ResourceServlet servlet = new ResourceServlet(factory);
        when(factory.createResourcePresenter(response)).thenReturn(presenter);
        when(factory.createShowResource(presenter)).thenReturn(useCase);
        servlet.service(request, response);
        verify(factory).createResourcePresenter(response);
        verify(factory).createShowResource(presenter);
        verify(useCase).call(1);
    }
}