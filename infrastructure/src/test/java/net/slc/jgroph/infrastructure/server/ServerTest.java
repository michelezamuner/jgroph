package net.slc.jgroph.infrastructure.server;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;
import java.net.InetSocketAddress;
import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class ServerTest
{
    @Rule public final ExpectedException exception = ExpectedException.none();
    @Mock private AsynchronousServerSocketChannel channel;
    @Mock private ClientFactory factory;
    @Mock private AsynchronousSocketChannel clientChannel;
    @InjectMocks private Server server;

    @Before
    public void setUp()
    {
        when(factory.create(clientChannel)).thenReturn(mock(Client.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void channelIsBoundToCorrectAddress()
            throws IOException
    {
        final String host = "0.0.0.0";
        final int port = 8000;
        server.listen(host, port, mock(Consumer.class));
        verify(channel).bind(new InetSocketAddress(host, port));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void channelWaitsForNewConnectionsAndInjectsTheServerObjectIntoTheHandler()
            throws IOException
    {
        server.listen("0.0.0.0", 8000, mock(Consumer.class));
        verify(channel).accept(eq(server), any(CompletionHandler.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void clientCallbackIsCalledWithActualClient()
            throws IOException
    {
        final Client client = mock(Client.class);
        when(factory.create(clientChannel)).thenReturn(client);

        doAnswer(invocation -> {
                final CompletionHandler<AsynchronousSocketChannel, Server> handler = invocation.getArgument(1);
                handler.completed(clientChannel, server);
                return null;
        }).doNothing().when(channel).accept(eq(server), any(CompletionHandler.class));

        final Consumer<Client> callback = mock(Consumer.class);
        server.listen("0.0.0.0", 8000, callback);

        verify(callback).accept(client);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void newAcceptIsCalledAfterPreviousOneReturned()
            throws IOException
    {
        doAnswer(invocation -> {
            final CompletionHandler<AsynchronousSocketChannel, Server> handler = invocation.getArgument(1);
            handler.completed(clientChannel, server);
            return null;
        }).doNothing().when(channel).accept(eq(server), any(CompletionHandler.class));

        final Consumer<Client> callback = mock(Consumer.class);
        server.listen("0.0.0.0", 8000, callback);

        final InOrder inOrder = inOrder(callback, channel);
        inOrder.verify(callback).accept(any(Client.class));
        inOrder.verify(channel).accept(eq(server), any(CompletionHandler.class));
    }

    @Test
    public void callbackCannotBeNull()
            throws IOException
    {
        exception.expect(MissingCallbackError.class);
        exception.expectMessage("Missing required callback.");
        server.listen("0.0.0.0", 8000, null);
    }
}