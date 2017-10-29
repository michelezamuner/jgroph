package net.slc.jgroph.adapters.remoteconsole;

import net.slc.jgroph.infrastructure.server.Client;
import net.slc.jgroph.infrastructure.server.Server;
import org.junit.Test;

import java.io.IOException;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BootstrapTest
{
    @Test
    @SuppressWarnings("unchecked")
    public void testServerIsAcceptingIncomingConnections()
            throws IOException
    {
        final String host = "0.0.0.0";
        final int  port = 8000;
        final Server server = mock(Server.class);
        final Bootstrap bootstrap = new Bootstrap(server);

        bootstrap.execute(host, port);

        verify(server).listen(eq(host), eq(port), any(Consumer.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMessageIsWrittenToClient()
            throws IOException
    {
        final String host = "0.0.0.0";
        final int port = 8000;
        final Client client = mock(Client.class);

        final Server server = mock(Server.class);
        doAnswer(invocation -> {
            final Consumer<Client> callback = invocation.getArgument(2);
            callback.accept(client);
            return null;
        }).doNothing().when(server).listen(eq(host), eq(port), any(Consumer.class));

        final Bootstrap bootstrap = new Bootstrap(server);
        bootstrap.execute(host, port);

        verify(client).write(eq("Hello, World!"), any(Consumer.class), any(Consumer.class));
    }
}