package net.slc.jgroph.bookmarks_services.adapters.http;

import com.github.javafaker.Faker;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.InetSocketAddress;

import static org.mockito.Mockito.*;

@SuppressWarnings("initialization")
@RunWith(MockitoJUnitRunner.class)
public class LauncherTest
{
    private final Faker faker = new Faker();
    @Mock private LauncherDependenciesFactory factory;
    @Mock private Server server;
    @Mock private ServletContextHandler handler;
    @Mock private ServletHolder holder;
    private Launcher launcher;

    @Before
    public void setUp()
    {
        launcher = new Launcher(factory);
    }

    @Test
    public void routerIsRegisteredWithServer()
            throws Exception
    {
        final String host = faker.internet().ipV4Address();
        final int port = faker.number().numberBetween(1025, 65535);
        final InetSocketAddress address = new InetSocketAddress(host, port);

        when(factory.createServer(address)).thenReturn(server);
        when(factory.createHandler(ServletContextHandler.SESSIONS)).thenReturn(handler);
        when(factory.createHolder(any(Router.class))).thenReturn(holder);

        launcher.launch(host, port);

        verify(handler).setContextPath("/");
        verify(handler).addServlet(holder, "/*");
        verify(server).setHandler(handler);
        verify(server).start();
        verify(server).join();
    }
}