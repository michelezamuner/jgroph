package net.slc.jgroph.infrastructure.server;

import org.junit.Test;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertTrue;

public class AcceptCompletionHandlerTest
{
    @Test
    @SuppressWarnings("unchecked")
    public void CallbackIsCalledWithActualClient()
    {
        final Server server = mock(Server.class);
        final Client client = mock(Client.class);
        final ClientFactory factory = mock(ClientFactory.class);
        final AsynchronousSocketChannel channel = mock(AsynchronousSocketChannel.class);
        when(factory.create(channel)).thenReturn(client);

        final Consumer<Client> callback = mock(Consumer.class);

        final CompletionHandler<AsynchronousSocketChannel, Server> handler =
                new AcceptCompletionHandler(factory, callback);
        handler.completed(channel, server);

        verify(callback).accept(client);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void newAcceptIsAutomaticallyCalledAfterServingThePreviousOne()
    {
        class Status
        {
            boolean callbackWasCalled = false;
            boolean acceptCalledAfterCallback = false;
        }

        final Status status = new Status();

        class ServerMock extends Server
        {
            private ServerMock()
            {
                super(null, null);
            }

            public void accept()
            {
                status.acceptCalledAfterCallback = status.callbackWasCalled;
            }
        }

        final ClientFactory factory = mock(ClientFactory.class);
        final Consumer<Client> callback = s -> status.callbackWasCalled = true;
        final AsynchronousSocketChannel channel = mock(AsynchronousSocketChannel.class);

        final CompletionHandler<AsynchronousSocketChannel, Server> handler =
                new AcceptCompletionHandler(factory, callback);
        handler.completed(channel, new ServerMock());

        assertTrue(status.callbackWasCalled);
        assertTrue(status.acceptCalledAfterCallback);
    }
}