package net.slc.jgroph.adapters.api;

import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRegistration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BootstrapTest
{
    @Test
    public void servletIsProperlyCreated()
    {
        final ServletRegistration.Dynamic registration = mock(ServletRegistration.Dynamic.class);
        final ServletContext context = mock(ServletContext.class);
        when(context.addServlet(eq("resources"), any(ResourceServlet.class))).thenReturn(registration);

        final ServletContextEvent event = mock(ServletContextEvent.class);
        when(event.getServletContext()).thenReturn(context);

        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.contextInitialized(event);
        verify(registration).addMapping(eq("/resources/*"));
    }
}