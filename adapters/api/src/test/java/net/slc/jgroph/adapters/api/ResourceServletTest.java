package net.slc.jgroph.adapters.api;

import com.github.javafaker.Faker;
import net.slc.jgroph.infrastructure.container.Container;
import net.slc.jgroph.application.ResourcePresenter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ResourceServletTest
{
    private Faker faker = new Faker();
    private ResourceServlet servlet;
    @Mock private Container container;
    @Mock private ResourceController controller;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private ApiResourcePresenter presenter;
    @Mock private ErrorPresenter errorPresenter;

    @Before
    public void setUp()
    {
        servlet = new ResourceServlet(container);
        when(container.make(ResourceController.class)).thenReturn(controller);
        when(request.getMethod()).thenReturn("GET");
    }

    @Test
    public void resourceRequestIsRoutedToShowResource()
            throws ServletException, IOException
    {
        final String id = String.valueOf(faker.number().randomNumber());
        when(request.getPathInfo()).thenReturn("/" + id);
        servlet.service(request, response);
        verify(controller).show(id);
    }

    @Test
    public void resourcePresenterIsBuiltFromTheRealResponse()
            throws ServletException, IOException
    {
        when(request.getPathInfo()).thenReturn("/");
        when(container.make(ApiResourcePresenter.class, response)).thenReturn(presenter);
        servlet.service(request, response);
        verify(container).bind(ResourcePresenter.class, presenter);
    }

    @Test
    public void errorPresenterIsBoundToTheRealResponse()
            throws ServletException, IOException
    {
        when(request.getPathInfo()).thenReturn("/");
        when(container.make(ErrorPresenter.class, response)).thenReturn(errorPresenter);
        servlet.service(request, response);
        verify(container).bind(ErrorPresenter.class, errorPresenter);
    }

    @Test
    public void routeToNowhereIfPathIsNull()
            throws ServletException, IOException
    {
        when(request.getPathInfo()).thenReturn(null);
        servlet.service(request, response);
        verifyZeroInteractions(controller);
    }
}