package net.slc.jgroph.infrastructure.server;

import static org.mockito.AdditionalMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.invocation.InvocationOnMock;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;
import java.net.InetSocketAddress;
import java.io.IOException;

public class ServerTest
{
    @Test
    @SuppressWarnings("unchecked")
    public void channelIsBoundToCorrectAddress()
            throws IOException
    {
        final String host = "0.0.0.0";
        final int port = 8000;

        final AsynchronousServerSocketChannel channel = mock(AsynchronousServerSocketChannel.class);
        final Server server = new Server(channel);
        server.listen(host, port, mock(Consumer.class));

        verify(channel).bind(new InetSocketAddress(host, port));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void channelWaitsForNewConnectionsAndInjectsTheServerObjectIntoTheHandler()
            throws IOException
    {
        final AsynchronousServerSocketChannel channel = mock(AsynchronousServerSocketChannel.class);
        final Server server = new Server(channel);
        server.listen("0.0.0.0", 8000, mock(Consumer.class));

        verify(channel).accept(ArgumentMatchers.eq(server), any(CompletionHandler.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void clientCallbackIsCalledAfterNewConnectionIsCompleted()
    {
        final AsynchronousServerSocketChannel channel = mock(AsynchronousServerSocketChannel.class);
        final Server server = new Server(channel);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final Server serverArg = invocation.getArgument(0);
                final CompletionHandler<AsynchronousSocketChannel, Server> handler = invocation.getArgument(1);
                handler.completed(mock(AsynchronousSocketChannel.class), serverArg);
                return null;
            }
        }).when(channel.accept(server, any(CompletionHandler.class)));

        final Consumer<Client> callback = mock(Consumer.class);
        server.listen("0.0.0.0", 8000, callback);

        verify(callback).accept(any(Client.class));
    }
}