package net.slc.jgroph.infrastructure.server;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class ReadCompletionHandlerTest
{
    private Faker faker;

    @Before
    public void setUp()
    {
        faker = new Faker();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void successCallbackIsCalledOnSuccessfulRead()
    {
        final String message = faker.lorem().sentence();
        final ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(BufferFactory.DEFAULT_CHARSET));
        final Consumer<String> success = mock(Consumer.class);

        final ReadCompletionHandler handler = new ReadCompletionHandler(buffer, success, null);
        handler.completed(0, mock(Reader.class));

        verify(success).accept(message);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handlerIsCalledWithTheRightCallbacks()
            throws MissingCallbackException
    {
        final ByteBuffer buffer = ByteBuffer.allocate(0);
        final Consumer<String> success = mock(Consumer.class);
        final Consumer<Throwable> failure = mock(Consumer.class);
        final Reader reader = mock(Reader.class);

        final ReadCompletionHandler handler = new ReadCompletionHandler(buffer, success, failure);
        handler.completed(0, reader);

        verify(reader).accept(success, failure);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void newReadIsAutomaticallyCalledAfterTheCallbackIsExecuted()
    {
        class Status
        {
            boolean callbackWasCalled = false;
            boolean readCalledAfterCallback = false;
        }

        final Status status = new Status();
        final ByteBuffer buffer = ByteBuffer.allocate(0);
        final Consumer<String> success = s -> status.callbackWasCalled = true;

        final ReadCompletionHandler handler = new ReadCompletionHandler(buffer, success, null);
        handler.completed(0, (s, f) -> status.readCalledAfterCallback = status.callbackWasCalled);

        assertTrue(status.callbackWasCalled);
        assertTrue(status.readCalledAfterCallback);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void successCallbackIsNotCalledIfNoBytesWereRead()
    {
        final ByteBuffer buffer = ByteBuffer.allocate(0);
        final Consumer<String> success = mock(Consumer.class);
        final ReadCompletionHandler handler = new ReadCompletionHandler(buffer, success, null);
        handler.completed(-1, mock(Reader.class));

        verifyZeroInteractions(success);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void failedCallbackIsCalledOnFailureWithTheRightThrowable()
    {
        final Throwable throwable = mock(Throwable.class);
        final ByteBuffer buffer = ByteBuffer.allocate(0);
        final Consumer<Throwable> failure = mock(Consumer.class);
        final ReadCompletionHandler handler = new ReadCompletionHandler(buffer, null, failure);
        handler.failed(throwable, null);

        verify(failure).accept(throwable);
    }
}