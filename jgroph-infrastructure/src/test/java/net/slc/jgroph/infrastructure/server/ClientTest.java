package net.slc.jgroph.infrastructure.server;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

public class ClientTest
{
    private Faker faker;

    @Before
    public void setUp()
    {
        faker = new Faker();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void channelIsUsedWithProperArgumentsOnRead()
            throws MissingCallbackException
    {
        final AsynchronousSocketChannel channel = mock(AsynchronousSocketChannel.class);

        final ByteBuffer buffer = mock(ByteBuffer.class);
        final BufferFactory bufferFactory = mock(BufferFactory.class);
        when(bufferFactory.createForRead()).thenReturn(buffer);

        final Consumer<String> success = mock(Consumer.class);
        final Consumer<Throwable> failure = mock(Consumer.class);

        final CompletionHandler<Integer, Reader> handler = mock(ReadCompletionHandler.class);
        final ClientHandlersFactory handlersFactory = mock(ClientHandlersFactory.class);
        when(handlersFactory.createReadHandler(buffer, success, failure)).thenReturn(handler);

        final Client client = new Client(channel, bufferFactory, handlersFactory);
        client.read(success, failure);

        verify(channel).read(buffer, mock(Reader.class), handler);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void channelIsUsedWithProperArgumentsOnWrite()
            throws MissingCallbackException
    {
        final String message = faker.lorem().sentence();
        final AsynchronousSocketChannel channel = mock(AsynchronousSocketChannel.class);

        final ByteBuffer buffer = mock(ByteBuffer.class);
        final BufferFactory bufferFactory = mock(BufferFactory.class);
        when(bufferFactory.createForWrite(message)).thenReturn(buffer);

        final Consumer<Integer> success = mock(Consumer.class);
        final Consumer<Throwable> failure = mock(Consumer.class);

        final CompletionHandler<Integer, Client> handler = mock(ReadCompletionHandler.class);
        final ClientHandlersFactory handlersFactory = mock(ClientHandlersFactory.class);
        when(handlersFactory.createWriteHandler(success, failure)).thenReturn(handler);

        final Client client = new Client(channel, bufferFactory, handlersFactory);
        client.write(message, success, failure);

        verify(channel).write(buffer, client, handler);
    }

    @Test(expected = MissingCallbackException.class)
    @SuppressWarnings("unchecked")
    public void successCallbackCannotBeNullOnRead()
            throws MissingCallbackException
    {
        final Client client = new Client(null, null, null);
        client.read(null, mock(Consumer.class));
    }

    @Test(expected = MissingCallbackException.class)
    @SuppressWarnings("unchecked")
    public void failureCallbackCannotBeNullOnRead()
            throws MissingCallbackException
    {
        final Client client = new Client(null, null, null);
        client.read(mock(Consumer.class), null);
    }

    @Test(expected = MissingCallbackException.class)
    @SuppressWarnings("unchecked")
    public void successCallbackCannotBeNullOnWrite()
            throws MissingCallbackException
    {
        final Client client = new Client(null, null, null);
        client.write("",null, mock(Consumer.class));
    }

    @Test(expected = MissingCallbackException.class)
    @SuppressWarnings("unchecked")
    public void failureCallbackCannotBeNullOnWrite()
            throws MissingCallbackException
    {
        final Client client = new Client(null, null, null);
        client.write("",mock(Consumer.class), null);
    }
}