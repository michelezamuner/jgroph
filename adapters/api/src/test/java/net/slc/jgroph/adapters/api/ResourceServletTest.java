package net.slc.jgroph.adapters.api;

import com.github.javafaker.Faker;
import net.slc.jgroph.infrastructure.container.Container;
import net.slc.jgroph.application.ResourcePresenter;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class ResourceServletTest
{
    private Faker faker;

    @Before
    public void setUp()
    {
        faker = new Faker();
    }

    @Test
    public void resourceRequestIsRoutedToShowResource()
            throws ServletException, IOException
    {
        final String id = String.valueOf(faker.number().randomNumber());

        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("/" + id);

        final ResourceController controller = mock(ResourceController.class);

        final Container container = mock(Container.class);
        when(container.make(ResourceController.class)).thenReturn(controller);

        final ResourceServlet servlet = new ResourceServlet(container);
        servlet.service(request, mock(HttpServletResponse.class));

        verify(controller).show(id);
    }

    @Test
    public void resourcePresenterIsBuiltFromTheRealResponse()
            throws ServletException, IOException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("/");

        final HttpServletResponse response = mock(HttpServletResponse.class);
        final ResourcePresenterAdapter presenter = mock(ResourcePresenterAdapter.class);

        final Container container = mock(Container.class);
        when(container.make(ResourcePresenterAdapter.class, response)).thenReturn(presenter);
        when(container.make(ResourceController.class)).thenReturn(mock(ResourceController.class));

        final ResourceServlet servlet = new ResourceServlet(container);
        servlet.service(request, response);

        verify(container).bind(ResourcePresenter.class, presenter);
    }

    @Test
    public void errorPresenterIsBoundToTheRealResponse()
            throws ServletException, IOException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("/");

        final HttpServletResponse response = mock(HttpServletResponse.class);
        final ErrorPresenter presenter = mock(ErrorPresenter.class);

        final Container container = mock(Container.class);
        when(container.make(ErrorPresenter.class, response)).thenReturn(presenter);
        when(container.make(ResourceController.class)).thenReturn(mock(ResourceController.class));

        final ResourceServlet servlet = new ResourceServlet(container);
        servlet.service(request, response);
        verify(container).bind(ErrorPresenter.class, presenter);
    }

    @Test
    public void routeToNowhereIfPathIsNull()
            throws ServletException, IOException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn(null);

        final ResourceController controller = mock(ResourceController.class);

        final Container container = mock(Container.class);
        when(container.make(ResourceController.class)).thenReturn(controller);

        final ResourceServlet servlet = new ResourceServlet(container);
        servlet.service(request, mock(HttpServletResponse.class));
        verifyZeroInteractions(controller);
    }
}