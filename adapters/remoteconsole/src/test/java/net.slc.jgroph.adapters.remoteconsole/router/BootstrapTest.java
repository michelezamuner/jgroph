package net.slc.jgroph.adapters.remoteconsole.router;

import com.github.javafaker.Faker;
import net.slc.jgroph.infrastructure.server.Client;
import net.slc.jgroph.infrastructure.server.Server;
import net.slc.jgroph.infrastructure.container.Container;
import net.slc.jgroph.providers.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BootstrapTest
{
    private final Faker faker = new Faker();
    private String method;
    private String prefix;
    private String path;
    @Mock private Server server;
    @Mock private Client client;
    @Mock private Container container;
    @Mock private Application application;
    @Mock private Router router;
    @InjectMocks private Bootstrap bootstrap;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp()
            throws IOException
    {
        method = "GET";
        prefix = "/resources";
        path = "/" + faker.number().randomNumber();

        when(container.make(Server.class)).thenReturn(server);
        when(container.make(Router.class)).thenReturn(router);

        // Simulate an asynchronous client read, calling the client read callback with the request string, as soon as
        // the read callback is registered with the client.
        doAnswer(invocation -> {
            final Consumer<String> callback = invocation.getArgument(0);
            callback.accept(method + " " + prefix + path);
            return null;
        }).doNothing().when(client).read(any(Consumer.class), any(Consumer.class));

        // Simulate an asynchronous server connection, calling the server listen callback with the mock client as soon
        // as the connect callback is registered with the server.
        doAnswer(invocation -> {
            final Consumer<Client> callback = invocation.getArgument(2);
            callback.accept(client);
            return null;
        }).doNothing().when(server).listen(anyString(), anyInt(), any(Consumer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void serverIsAcceptingIncomingConnections()
            throws IOException
    {
        final String host = faker.internet().ipV4Address();
        final int port = faker.number().numberBetween(1025, 65535);
        bootstrap.execute(host, port);
        verify(server).listen(eq(host), eq(port), any(Consumer.class));
    }

    @Test
    public void applicationIsBootstrappedWithContainer()
            throws IOException
    {
        bootstrap.execute("0.0.0.0", 8000);
        verify(application).bootstrap(container);
    }

    @Test
    public void routerIsUsedWithRealRequest()
            throws IOException
    {
        bootstrap.execute("0.0.0.0", 8000);

        verify(router).route(argThat(request ->
            request.getMethod().toString().equals(method) &&
                    request.getPrefix().equals(prefix) &&
                    request.getPath().equals(path)
        ), any(Response.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void routerIsUsedWithRealResponse()
            throws IOException
    {
        final String responseMessage = faker.lorem().sentence();

        // Simulate an asynchronous client response, calling the router response callback with the response message, as
        // soon as the response callback is registered with the router.
        doAnswer(invocation -> {
            final Response response = invocation.getArgument(1);
            response.write(responseMessage);
            return null;
        }).when(router).route(any(Request.class), any(Response.class));

        bootstrap.execute("0.0.0.0", 8000);

        verify(client).write(eq(responseMessage), any(Consumer.class), any(Consumer.class));
    }
}