package net.slc.jgroph.infrastructure.server;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

import org.junit.Test;
import org.junit.Before;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;
import java.net.InetSocketAddress;
import java.io.IOException;

public class ServerTest
{
    @Mock
    private AsynchronousServerSocketChannel channel;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void channelIsBoundToCorrectAddress()
            throws IOException, MissingCallbackException
    {
        final String host = "0.0.0.0";
        final int port = 8000;

        final Server server = new Server(channel);
        server.listen(host, port, mock(Consumer.class));

        verify(channel).bind(new InetSocketAddress(host, port));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void channelWaitsForNewConnectionsAndInjectsTheServerObjectIntoTheHandler()
            throws IOException, MissingCallbackException
    {
        final Server server = new Server(channel);
        server.listen("0.0.0.0", 8000, mock(Consumer.class));

        verify(channel).accept(eq(server), any(CompletionHandler.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void clientCallbackIsCalledWithActualClient()
            throws IOException, MissingCallbackException
    {
        final AsynchronousSocketChannel clientChannel = mock(AsynchronousSocketChannel.class);
        final Client client = mock(Client.class);
        final ClientFactory factory = mock(ClientFactory.class);
        when(factory.create(clientChannel)).thenReturn(client);

        final Server server = new Server(channel, factory);
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
            throws IOException, MissingCallbackException
    {
        final Server server = new Server(channel);
        doAnswer(invocation -> {
            final CompletionHandler<AsynchronousSocketChannel, Server> handler = invocation.getArgument(1);
            handler.completed(mock(AsynchronousSocketChannel.class), server);
            return null;
        }).doNothing().when(channel).accept(eq(server), any(CompletionHandler.class));

        final Consumer<Client> callback = mock(Consumer.class);
        server.listen("0.0.0.0", 8000, callback);

        final InOrder inOrder = inOrder(callback, channel);
        inOrder.verify(callback).accept(any(Client.class));
        inOrder.verify(channel).accept(eq(server), any(CompletionHandler.class));
    }

    @Test(expected = MissingCallbackException.class)
    public void callbackCannotBeNull()
            throws IOException, MissingCallbackException
    {
        final Server server = new Server(channel);
        server.listen("0.0.0.0", 8000, null);
    }
}