package net.slc.jgroph.adapters.api;

import net.slc.jgroph.configuration.Provider;
import net.slc.jgroph.infrastructure.container.Container;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRegistration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("initialization")
public class BootstrapTest
{
    @Mock private Provider provider;
    @Mock private Container container;
    @Mock private ServletRegistration.Dynamic registration;
    @Mock private ServletContext context;
    @Mock private ServletContextEvent event;
    @Mock private ResourceServlet servlet;
    @InjectMocks private Bootstrap bootstrap;

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
        bootstrap.contextInitialized(event);
        verify(provider).bootstrap(container);
    }

    @Test
    public void resourcesRouteIsConfigured()
    {
        bootstrap.contextInitialized(event);
        verify(registration).addMapping(eq("/resources/*"));
    }
}