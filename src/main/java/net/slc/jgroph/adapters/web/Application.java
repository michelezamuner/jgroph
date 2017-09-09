package net.slc.jgroph.adapters.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServlet;

@WebListener
public class Application implements ServletContextListener
{
    @Override
    public void contextInitialized(final ServletContextEvent event)
    {
        HttpServlet servlet = new ResourceServlet(new Factory());

        final ServletContext context = event.getServletContext();
        context.addServlet("resource", servlet).addMapping("/resource*");
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event)
    {

    }
}