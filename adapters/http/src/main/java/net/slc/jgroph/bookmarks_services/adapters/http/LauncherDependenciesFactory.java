package net.slc.jgroph.bookmarks_services.adapters.http;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;
import java.net.InetSocketAddress;

class LauncherDependenciesFactory
{
    Server createServer(final InetSocketAddress address)
    {
        return new Server(new org.eclipse.jetty.server.Server(address));
    }

    ServletContextHandler createHandler(final int type)
    {
        return new ServletContextHandler(type);
    }

    ServletHolder createHolder(final Servlet servlet)
    {
        return new ServletHolder(servlet);
    }
}