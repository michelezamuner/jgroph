package net.slc.jgroph.bookmarks_services.adapters.http;

import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * This wrapper is needed to be able to unit test the Launcher, since some methods of the Jetty Server are final.
 */
class Server
{
    private final org.eclipse.jetty.server.Server server;

    Server(final org.eclipse.jetty.server.Server server)
    {
        this.server = server;
    }

    void setHandler(final ServletContextHandler handler)
    {
        server.setHandler(handler);
    }

    void start()
            throws Exception
    {
        server.start();
    }

    void join()
            throws InterruptedException
    {
        server.join();
    }
}