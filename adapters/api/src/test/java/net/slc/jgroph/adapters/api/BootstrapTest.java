package net.slc.jgroph.adapters.api;

import net.slc.jgroph.providers.Application;
import net.slc.jgroph.infrastructure.container.Container;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRegistration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BootstrapTest
{
    @Test
    public void applicationIsBootstrappedWithContainer()
    {
        final Application application = mock(Application.class);
        final Container container = mock(Container.class);
        when(container.make(ResourceServlet.class)).thenReturn(mock(ResourceServlet.class));

        final ServletRegistration.Dynamic registration = mock(ServletRegistration.Dynamic.class);
        final ServletContext context = mock(ServletContext.class);
        when(context.addServlet(anyString(), any(ResourceServlet.class))).thenReturn(registration);

        final ServletContextEvent event = mock(ServletContextEvent.class);
        when(event.getServletContext()).thenReturn(context);

        final Bootstrap bootstrap = new Bootstrap(container, application);
        bootstrap.contextInitialized(event);

        verify(application).bootstrap(container);
    }

    @Test
    public void resourcesRouteIsConfigured()
    {
        final ResourceServlet servlet = mock(ResourceServlet.class);

        final Container container = mock(Container.class);
        when(container.make(ResourceServlet.class)).thenReturn(servlet);

        final ServletRegistration.Dynamic registration = mock(ServletRegistration.Dynamic.class);
        final ServletContext context = mock(ServletContext.class);
        when(context.addServlet(anyString(), eq(servlet))).thenReturn(registration);

        final ServletContextEvent event = mock(ServletContextEvent.class);
        when(event.getServletContext()).thenReturn(context);

        final Bootstrap bootstrap = new Bootstrap(container, mock(Application.class));
        bootstrap.contextInitialized(event);

        verify(registration).addMapping(eq("/resources/*"));
    }
}