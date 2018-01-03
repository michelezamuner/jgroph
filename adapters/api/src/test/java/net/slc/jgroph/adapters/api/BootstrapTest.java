package net.slc.jgroph.adapters.api;

import net.slc.jgroph.providers.Application;
import net.slc.jgroph.infrastructure.container.Container;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRegistration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BootstrapTest
{
    @Mock private Application application;
    @Mock private Container container;
    @Mock private ServletRegistration.Dynamic registration;
    @Mock private ServletContext context;
    @Mock private ServletContextEvent event;
    @Mock private ResourceServlet servlet;

    @Before
    public void setUp()
    {
        when(container.make(ResourceServlet.class)).thenReturn(servlet);
        when(context.addServlet(anyString(), eq(servlet))).thenReturn(registration);
        when(event.getServletContext()).thenReturn(context);
    }

    @Test
    public void applicationIsBootstrappedWithContainer()
    {
        final Bootstrap bootstrap = new Bootstrap(container, application);
        bootstrap.contextInitialized(event);

        verify(application).bootstrap(container);
    }

    @Test
    public void resourcesRouteIsConfigured()
    {
        final Bootstrap bootstrap = new Bootstrap(container, any(Application.class));
        bootstrap.contextInitialized(event);

        verify(registration).addMapping(eq("/resources/*"));
    }
}