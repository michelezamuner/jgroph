package net.slc.jgroph.infrastructure.server;

import com.github.javafaker.Faker;
import org.junit.Test;
import org.junit.Before;

import static org.mockito.ArgumentMatchers.*;

import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import java.util.function.Consumer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.mockito.MockitoAnnotations;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.Mockito.*;

public class ClientTest
{
    private final Faker faker = new Faker();

    @Mock
    private AsynchronousSocketChannel channel;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void channelIsUsedWithProperArgumentsOnRead()
    {
        final Client client = new Client(channel);

        client.read(mock(Consumer.class));
        verify(channel).read(
                any(ByteBuffer.class),
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
        final ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(UTF_8));
        final Client client = new Client(channel);

        client.write(message);
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
    public void successCallbackIsCalledOnSuccessfulRead()
    {
        final String message = faker.lorem().sentence();
        final Client client = new Client(channel);

        doAnswer(invocation -> {
            final ByteBuffer buffer = invocation.getArgument(0);
            final CompletionHandler<Integer, Client> handler = invocation.getArgument(4);

            buffer.put(message.getBytes(UTF_8));
            System.out.println(new String(buffer.array(), UTF_8));
            handler.completed(0, client);
            return null;
        }).doNothing().when(channel).read(any(ByteBuffer.class), eq(0L), eq(null), eq(client), any(CompletionHandler.class));

        final Consumer<String> success = mock(Consumer.class);
        client.read(success);

        verify(success).accept(eq(message));
    }
}