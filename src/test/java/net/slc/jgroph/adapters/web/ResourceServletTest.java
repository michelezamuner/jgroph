package net.slc.jgroph.adapters.web;

import net.slc.jgroph.application.ShowResource;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ResourceServletTest
{
    @Test
    public void theShowResourceUseCaseIsProperlyCalled()
            throws IOException, ServletException
    {
        final ShowResource useCase = mock(ShowResource.class);
        final HttpServlet servlet = new ResourceServlet(useCase);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getPathInfo()).thenReturn("/1");
        servlet.service(request, response);
        verify(useCase).call(1);

        when(request.getPathInfo()).thenReturn("/123456");
        servlet.service(request, response);
        verify(useCase).call(123456);
    }
}