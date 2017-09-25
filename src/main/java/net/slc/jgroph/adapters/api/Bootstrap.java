package net.slc.jgroph.adapters.api;

import net.slc.jgroph.Application;
import net.slc.jgroph.infrastructure.container.Container;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Bootstrap implements ServletContextListener
{
    @Override
    public void contextInitialized(final ServletContextEvent event)
    {
        final Container container = new Container();
        final Application application = new Application();
        application.bootstrap(container);

        final ResourceServlet servlet = new ResourceServlet(container);
        final ServletContext context = event.getServletContext();
        context.addServlet("resources", servlet).addMapping("/resources/*");
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event)
    {

    }
}