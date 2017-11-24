package net.slc.jgroph.infrastructure.server;

import com.github.javafaker.Faker;
import org.junit.Test;
import org.junit.Before;

import static org.mockito.ArgumentMatchers.*;

import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.function.Consumer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.mockito.MockitoAnnotations;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Mockito.*;
import static net.slc.jgroph.infrastructure.server.Client.BUFFER_SIZE;

public class ClientTest
{
    private final Faker faker = new Faker();

    @Mock
    private AsynchronousSocketChannel channel;

    private Client client;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
        client = new Client(channel);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void channelIsUsedWithProperArgumentsOnRead()
    {
        final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        client.read(mock(Consumer.class), mock(Consumer.class));
        verify(channel).read(
                eq(buffer),
                eq(0L),
                eq(null),
                eq(client),
                any(CompletionHandler.class)
        );
    }

    @Test(expected = MissingCallbackError.class)
    @SuppressWarnings("unchecked")
    public void successCallbackCannotBeNullOnRead()
    {
        client.read(null, mock(Consumer.class));
    }

    @Test(expected = MissingCallbackError.class)
    @SuppressWarnings("unchecked")
    public void failureCallbackCannotBeNullOnRead()
    {
        client.read(mock(Consumer.class), null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void successCallbackIsCalledWithProperArgumentsOnRead()
    {
        final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        final String message = faker.lorem().sentence();

        doAnswer(invocation -> {
            final ByteBuffer buf = invocation.getArgument(0);
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);

            buf.put(message.getBytes(UTF_8));
            handler.completed(0, client);
            return null;
        }).doNothing().when(channel).read(
                eq(buffer),
                eq(0L),
                eq(null),
                eq(client),
                any(CompletionHandler.class)
        );

        final Consumer<String> onSuccess = mock(Consumer.class);
        client.read(onSuccess, mock(Consumer.class));

        verify(onSuccess).accept(message);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void newlinesAreRemovedFromReadMessage()
    {
        final String trimmedMessage = faker.lorem().sentence();
        final String message = trimmedMessage + '\n';
        final Consumer<String> onSuccess = mock(Consumer.class);

        doAnswer(invocation -> {
            final ByteBuffer buf = invocation.getArgument(0);
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);

            buf.put(message.getBytes(UTF_8));
            handler.completed(0, client);
            return null;
        }).doNothing().when(channel).read(
                any(ByteBuffer.class),
                eq(0L),
                eq(null),
                eq(client),
                any(CompletionHandler.class)
        );

        client.read(onSuccess, mock(Consumer.class));

        verify(onSuccess).accept(trimmedMessage);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void successCallbackIsNotCalledIfNoBytesWereRead()
    {
        doAnswer(invocation -> {
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);
            handler.completed(-1, client);
            return null;
        }).doNothing().when(channel).read(
                any(ByteBuffer.class),
                eq(0L),
                eq(null),
                eq(client),
                any(CompletionHandler.class)
        );

        final Consumer<String> onSuccess = mock(Consumer.class);

        client.read(onSuccess, mock(Consumer.class));

        verifyZeroInteractions(onSuccess);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void failedCallbackIsCalledWithProperArgumentsOnRead()
    {
        final Throwable exception = mock(Throwable.class);

        doAnswer(invocation -> {
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);
            handler.failed(exception, client);
            return null;
        }).doNothing().when(channel).read(
                any(ByteBuffer.class),
                eq(0L),
                eq(null),
                eq(client),
                any(CompletionHandler.class)
        );

        final Consumer<Throwable> onFailure= mock(Consumer.class);
        client.read(mock(Consumer.class), onFailure);

        verify(onFailure).accept(exception);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void newReadIsCalledWithNewBufferAfterCallbackIsExecuted()
    {
        final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        doAnswer(invocation -> {
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);
            handler.completed(0, client);
            return null;
        }).doNothing().when(channel).read(
                eq(buffer),
                eq(0L),
                eq(null),
                eq(client),
                any(CompletionHandler.class)
        );

        final Consumer<String> onSuccess = mock(Consumer.class);

        client.read(onSuccess, mock(Consumer.class));

        final InOrder inOrder = inOrder(onSuccess, channel);
        inOrder.verify(onSuccess).accept(anyString());
        inOrder.verify(channel).read(
                argThat(buf -> buf.limit() == BUFFER_SIZE && buf.position() == 0),
                eq(0L),
                eq(null),
                eq(client),
                any(CompletionHandler.class)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void channelIsUsedWithProperArgumentsOnWrite()
    {
        final String message = faker.lorem().sentence();
        final ByteBuffer buffer = ByteBuffer.wrap((message + '\n').getBytes(UTF_8));

        client.write(message, mock(Consumer.class), mock(Consumer.class));
        verify(channel).write(
                eq(buffer),
                eq(0L),
                eq(null),
                eq(client),
                any(CompletionHandler.class)
        );
    }

    @Test(expected = MissingCallbackError.class)
    @SuppressWarnings("unchecked")
    public void successCallbackCannotBeNullOnWrite()
    {
        client.write("", null, mock(Consumer.class));
    }

    @Test(expected = MissingCallbackError.class)
    @SuppressWarnings("unchecked")
    public void failureCallbackCannotBeNullOnWrite()
    {
        client.write("", mock(Consumer.class), null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void successCallbackIsCalledWithProperArgumentsOnWrite()
    {
        final String message = this.faker.lorem().sentence();
        final ByteBuffer buffer = ByteBuffer.wrap((message + '\n').getBytes(UTF_8));
        final int bytesWritten = buffer.limit();

        doAnswer(invocation -> {
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);
            handler.completed(bytesWritten, client);
            return null;
        }).when(channel).write(eq(buffer), eq(0L), eq(null), eq(client), any(CompletionHandler.class));

        final Consumer<Integer> onSuccess = mock(Consumer.class);
        client.write(message, onSuccess, mock(Consumer.class));

        verify(onSuccess).accept(eq(bytesWritten));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void failedCallbackIsCalledWithProperArgumentsOnWrite()
    {
        final Throwable exception = mock(Throwable.class);

        doAnswer(invocation -> {
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);
            handler.failed(exception, client);
            return null;
        }).when(channel).write(
                any(ByteBuffer.class),
                eq(0L),
                eq(null),
                eq(client),
                any(CompletionHandler.class)
        );

        final Consumer<Throwable> onFailure = mock(Consumer.class);

        client.write("", mock(Consumer.class), onFailure);

        verify(onFailure).accept(exception);
    }
}