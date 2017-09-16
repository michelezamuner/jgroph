package net.slc.jgroph.adapters.web;

import net.slc.jgroph.adapters.App;
import net.slc.jgroph.adapters.AppException;
import net.slc.jgroph.adapters.inmemorystorage.ResourceRepository;
import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.domain.ResourceId;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.*;

public class ResourceServletTest
{
    @Test
    public void resourceRequestIsRoutedToShowResource()
            throws ServletException, IOException, AppException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");

        final HttpServletResponse response = mock(HttpServletResponse.class);
        final ResourceController controller = mock(ResourceController.class);

        final App app = mock(App.class);
        when(app.make(ResourceController.class)).thenReturn(controller);

        final ResourceServlet servlet = new ResourceServlet(app);
        servlet.service(request, response);
        verify(controller).show(request, response);
    }

    @Test
    public void resourcePresenterIsBuiltFromTheRealResponse()
            throws ServletException, IOException, AppException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");

        final HttpServletResponse response = mock(HttpServletResponse.class);
        final ResourcePresenter presenter = mock(ResourcePresenter.class);

        final App app = mock(App.class);
        when(app.make(ResourcePresenter.class, response)).thenReturn(presenter);
        when(app.make(ResourceController.class)).thenReturn(mock(ResourceController.class));

        final ResourceServlet servlet = new ResourceServlet(app);
        servlet.service(request, response);
        verify(app).bind(eq(net.slc.jgroph.application.ResourcePresenter.class), eq(presenter));
    }

    @Test
    public void errorPresenterIsBoundToTheRealResponse()
            throws ServletException, IOException, AppException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");

        final HttpServletResponse response = mock(HttpServletResponse.class);
        final ErrorPresenter presenter = mock(ErrorPresenter.class);

        final App app = mock(App.class);
        when(app.make(ErrorPresenter.class, response)).thenReturn(presenter);
        when(app.make(ResourceController.class)).thenReturn(mock(ResourceController.class));

        final ResourceServlet servlet = new ResourceServlet(app);
        servlet.service(request, response);
        verify(app).bind(eq(ErrorPresenter.class), eq(presenter));
    }

    @Test
    public void resourceRepositoryIsBound()
            throws ServletException, IOException, AppException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");

        final ResourceRepository repository = mock(ResourceRepository.class);
        final ResourceRepositoryData data = mock(ResourceRepositoryData.class);

        final App app = mock(App.class);
        when(app.make(ResourceRepositoryData.class)).thenReturn(data);
        when(app.make(ResourceRepository.class, data)).thenReturn(repository);
        when(app.make(ResourceController.class)).thenReturn(mock(ResourceController.class));

        final ResourceServlet servlet = new ResourceServlet(app);
        servlet.service(request, mock(HttpServletResponse.class));
        verify(app).bind(eq(net.slc.jgroph.application.ResourceRepository.class), eq(repository));
    }

    @Test(expected = ServletException.class)
    public void appExceptionsAreConvertedToServletExceptions()
            throws ServletException, IOException
    {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getMethod()).thenReturn("GET");
        final App app = mock(App.class);
        doThrow(AppException.class).when(app).bind(any(), any());

        final ResourceServlet servlet = new ResourceServlet(app);
        servlet.service(request, mock(HttpServletResponse.class));
    }
}