package net.slc.jgroph.adapters.api;

import net.slc.jgroph.configuration.Provider;
import net.slc.jgroph.infrastructure.container.Container;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Bootstrap implements ServletContextListener
{
    private final Container container;
    private final Provider provider;

    Bootstrap(@Nullable final Container container, @Nullable final Provider provider)
    {
        this.container = container == null ? new Container() : container;
        this.provider = provider == null ? new Provider() : provider;
    }

    public Bootstrap()
    {
        this(null, null);
    }

    @Override
    public void contextInitialized(final ServletContextEvent event)
    {
        provider.bootstrap(container);

        event.getServletContext()
                .addServlet("resources", container.make(ResourceServlet.class))
                .addMapping("/resources/*");
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event)
    {

    }
}