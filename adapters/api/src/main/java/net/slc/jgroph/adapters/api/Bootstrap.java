package net.slc.jgroph.adapters.api;

import net.slc.jgroph.providers.Application;
import net.slc.jgroph.infrastructure.container.Container;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Bootstrap implements ServletContextListener
{
    private final Container container;
    private final Application application;

    Bootstrap(@Nullable final Container container, @Nullable final Application application)
    {
        this.container = container == null ? new Container() : container;
        this.application = application == null ? new Application() : application;
    }

    public Bootstrap()
    {
        this(null, null);
    }

    @Override
    public void contextInitialized(final ServletContextEvent event)
    {
        application.bootstrap(container);

        event.getServletContext()
                .addServlet("resources", container.make(ResourceServlet.class))
                .addMapping("/resources/*");
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event)
    {

    }
}