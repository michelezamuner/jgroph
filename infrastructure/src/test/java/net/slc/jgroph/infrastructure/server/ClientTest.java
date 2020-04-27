package net.slc.jgroph.infrastructure.server;

import com.github.javafaker.Faker;
import org.junit.Rule;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.*;

import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.function.Consumer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Mockito.*;
import static net.slc.jgroph.infrastructure.server.Client.BUFFER_SIZE;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("initialization")
public class ClientTest
{
    private final Faker faker = new Faker();
    private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    private String message = faker.lorem().sentence();
    @Rule public final ExpectedException exception = ExpectedException.none();
    @Mock private AsynchronousSocketChannel channel;
    @Mock private Consumer<String> onReadSuccess;
    @Mock private Consumer<Integer> onWriteSuccess;
    @Mock private Consumer<Throwable> onFailure;
    @InjectMocks private Client client;

    @Test
    @SuppressWarnings("unchecked")
    public void channelIsUsedWithProperArgumentsOnRead()
    {
        client.read(onReadSuccess, onFailure);
        verify(channel).read(
                eq(buffer),
                eq(0L),
                eq(null),
                eq(client),
                any(CompletionHandler.class)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void successCallbackCannotBeNullOnRead()
    {
        exception.expect(MissingCallbackError.class);
        exception.expectMessage("Callbacks cannot be null.");
        client.read(null, onFailure);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void failureCallbackCannotBeNullOnRead()
    {
        exception.expect(MissingCallbackError.class);
        exception.expectMessage("Callbacks cannot be null.");
        client.read(onReadSuccess, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void successCallbackIsCalledWithProperArgumentsOnRead()
    {
        simulateChannelRead(invocation -> {
            final ByteBuffer buf = invocation.getArgument(0);
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);

            buf.put(message.getBytes(UTF_8));
            handler.completed(0, client);
            return null;
        });

        client.read(onReadSuccess, onFailure);
        verify(onReadSuccess).accept(message);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void newlinesAreRemovedFromReadMessage()
    {
        final String trimmedMessage = message;
        message = message + '\n';
        simulateChannelRead(invocation -> {
            final ByteBuffer buf = invocation.getArgument(0);
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);

            buf.put(message.getBytes(UTF_8));
            handler.completed(0, client);
            return null;
        });

        client.read(onReadSuccess, onFailure);
        verify(onReadSuccess).accept(trimmedMessage);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void successCallbackIsNotCalledIfNoBytesWereRead()
    {
        simulateChannelRead(invocation -> {
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);
            handler.completed(-1, client);
            return null;
        });

        client.read(onReadSuccess, onFailure);
        verifyZeroInteractions(onReadSuccess);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void failedCallbackIsCalledWithProperArgumentsOnRead()
    {
        final Throwable exception = mock(Throwable.class);
        simulateChannelRead(invocation -> {
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);
            handler.failed(exception, client);
            return null;
        });

        client.read(onReadSuccess, onFailure);
        verify(onFailure).accept(exception);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void newReadIsCalledWithNewBufferAfterCallbackIsExecuted()
    {
        simulateChannelRead(invocation -> {
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);
            handler.completed(0, client);
            return null;
        });

        client.read(onReadSuccess, onFailure);

        final InOrder inOrder = inOrder(onReadSuccess, channel);
        inOrder.verify(onReadSuccess).accept(anyString());
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
        buffer = ByteBuffer.wrap((message + '\n').getBytes(UTF_8));
        client.write(message, onWriteSuccess, onFailure);
        verify(channel).write(
                eq(buffer),
                eq(0L),
                eq(null),
                eq(client),
                any(CompletionHandler.class)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void successCallbackCannotBeNullOnWrite()
    {
        exception.expect(MissingCallbackError.class);
        exception.expectMessage("Callbacks cannot be null.");
        client.write("", null, onFailure);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void failureCallbackCannotBeNullOnWrite()
    {
        exception.expect(MissingCallbackError.class);
        exception.expectMessage("Callbacks cannot be null.");
        client.write("", onWriteSuccess, null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void successCallbackIsCalledWithProperArgumentsOnWrite()
    {
        buffer = ByteBuffer.wrap((message + '\n').getBytes(UTF_8));
        final int bytesWritten = buffer.limit();
        simulateChannelWrite(invocation -> {
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);
            handler.completed(bytesWritten, client);
            return null;
        });

        client.write(message, onWriteSuccess, onFailure);
        verify(onWriteSuccess).accept(eq(bytesWritten));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void failedCallbackIsCalledWithProperArgumentsOnWrite()
    {
        final Throwable exception = mock(Throwable.class);
        simulateChannelWrite(invocation -> {
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);
            handler.failed(exception, client);
            return null;
        });

        client.write("", onWriteSuccess, onFailure);
        verify(onFailure).accept(exception);
    }

    @SuppressWarnings("unchecked")
    private void simulateChannelRead(final Answer callback)
    {
        doAnswer(callback)
                .doNothing()
                .when(channel)
                .read(eq(buffer), eq(0L), eq(null), eq(client), any(CompletionHandler.class));
    }

    @SuppressWarnings("unchecked")
    private void simulateChannelWrite(final Answer callback)
    {
        doAnswer(callback)
                .when(channel)
                .write(any(ByteBuffer.class), eq(0L), eq(null), eq(client), any(CompletionHandler.class));
    }
}