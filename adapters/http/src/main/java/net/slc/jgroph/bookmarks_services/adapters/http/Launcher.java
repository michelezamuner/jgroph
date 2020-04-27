package net.slc.jgroph.bookmarks_services.adapters.http;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.net.InetSocketAddress;

public class Launcher
{
    private final LauncherDependenciesFactory factory;

    public static void main(final String[] args)
            throws Exception
    {
        if (args.length < 2) {
            throw new IllegalArgumentException("Server host and port are required.");
        }

        new Launcher(new LauncherDependenciesFactory()).launch(args[0], Integer.parseInt(args[1]));
    }

    Launcher(final LauncherDependenciesFactory factory)
    {
        this.factory = factory;
    }

    void launch(final String host, final int port)
            throws Exception
    {
        final Server server = factory.createServer(new InetSocketAddress(host, port));
        final ServletContextHandler handler = factory.createHandler(ServletContextHandler.SESSIONS);
        final ServletHolder holder = factory.createHolder(new Router());

        handler.setContextPath("/");
        server.setHandler(handler);
        handler.addServlet(holder, "/*");
        server.start();
        server.join();
    }
}