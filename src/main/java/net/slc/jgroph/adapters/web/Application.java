package net.slc.jgroph.adapters.web;

import net.slc.jgroph.application.ShowResource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Application implements ServletContextListener
{
    @Override
    public void contextInitialized(final ServletContextEvent event)
    {
        ShowResource useCase = new ShowResource();

        final ServletContext context = event.getServletContext();
        context.addServlet("resource", new ResourceServlet(useCase)).addMapping("/resource*");
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event)
    {

    }
}