package net.slc.jgroph.adapters.remoteconsole.router;

import com.github.javafaker.Faker;
import net.slc.jgroph.adapters.remoteconsole.router.*;
import net.slc.jgroph.infrastructure.server.Client;
import net.slc.jgroph.infrastructure.server.Server;
import net.slc.jgroph.infrastructure.container.Container;
import net.slc.jgroph.providers.Application;
import org.junit.Test;

import java.io.IOException;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class BootstrapTest
{
    private final Faker faker = new Faker();

    @Test
    @SuppressWarnings("unchecked")
    public void serverIsAcceptingIncomingConnections()
            throws IOException
    {
        final String host = faker.internet().ipV4Address();
        final int port = faker.number().numberBetween(1025, 65535);

        final Server server = mock(Server.class);
        final Container container = mock(Container.class);
        when(container.make(Server.class)).thenReturn(server);

        final Bootstrap bootstrap = new Bootstrap(container, mock(Application.class));
        bootstrap.execute(host, port);

        verify(server).listen(eq(host), eq(port), any(Consumer.class));
    }

    @Test
    public void applicationIsBootstrappedWithContainer()
            throws IOException
    {
        final Application application = mock(Application.class);
        final Container container = mock(Container.class);
        when(container.make(Server.class)).thenReturn(mock(Server.class));

        final Bootstrap bootstrap = new Bootstrap(container, application);
        bootstrap.execute("0.0.0.0", 8000);

        verify(application).bootstrap(container);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void routerIsUsedWithRealRequest()
            throws IOException
    {
        final String method = "GET";
        final String prefix = "/resources";
        final String path = "/" + faker.number().randomNumber();

        final Client client = mock(Client.class);
        doAnswer(invocation -> {
            final Consumer<String> callback = invocation.getArgument(0);
            callback.accept(method + " " + prefix + path);
           return null;
        }).doNothing().when(client).read(any(Consumer.class), any(Consumer.class));

        final Server server = mock(Server.class);
        doAnswer(invocation -> {
            final Consumer<Client> callback = invocation.getArgument(2);
            callback.accept(client);
            return null;
        }).doNothing().when(server).listen(anyString(), anyInt(), any(Consumer.class));

        final Router router = mock(Router.class);

        final Container container = mock(Container.class);
        when(container.make(Server.class)).thenReturn(server);
        when(container.make(Router.class)).thenReturn(router);

        final Bootstrap bootstrap = new Bootstrap(container, mock(Application.class));
        bootstrap.execute("0.0.0.0", 8000);

        verify(router).route(argThat(response ->
            response.getMethod().equals(Method.valueOf(method)) &&
                    response.getPrefix().equals(prefix) &&
                    response.getPath().equals(path)
        ), any(Response.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void routerIsUsedWithRealResponse()
            throws IOException
    {
        final String responseMessage = faker.lorem().sentence();

        final Client client = mock(Client.class);
        doAnswer(invocation -> {
            final Consumer<String> callback = invocation.getArgument(0);
            callback.accept("GET /resources/1");
            return null;
        }).doNothing().when(client).read(any(Consumer.class), any(Consumer.class));

        final Server server = mock(Server.class);
        doAnswer(invocation -> {
            final Consumer<Client> callback = invocation.getArgument(2);
            callback.accept(client);
            return null;
        }).doNothing().when(server).listen(anyString(), anyInt(), any(Consumer.class));

        final Router router = mock(Router.class);
        doAnswer(invocation -> {
            final Response response = invocation.getArgument(1);
            response.write(responseMessage);
            return null;
        }).when(router).route(any(Request.class), any(Response.class));

        final Container container = mock(Container.class);
        when(container.make(Server.class)).thenReturn(server);
        when(container.make(Router.class)).thenReturn(router);

        final Bootstrap bootstrap = new Bootstrap(container, mock(Application.class));
        bootstrap.execute("0.0.0.0", 8000);

        verify(client).write(eq(responseMessage), any(Consumer.class), any(Consumer.class));
    }
}