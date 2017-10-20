package net.slc.jgroph.infrastructure.server;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;
import java.nio.ByteBuffer;
import static java.nio.charset.StandardCharsets.UTF_8;

public class Client
{
    public static final int BUFFER_SIZE = 2048;

    private final AsynchronousSocketChannel channel;

    public Client(final AsynchronousSocketChannel channel)
    {
        this.channel = channel;
    }

    public void read(final Consumer<String> onSuccess, final Consumer<Throwable> onFailure)
            throws MissingCallbackException
    {
        if (onSuccess == null || onFailure == null) {
            throw new MissingCallbackException("Callbacks cannot be null.");
        }

        readWithNonNullCallbacks(onSuccess, onFailure);
    }

    public void write(final String message, final Consumer<Integer> onSuccess, final Consumer<Throwable> onFailure)
            throws MissingCallbackException
    {
        if (onSuccess == null || onFailure == null) {
            throw new MissingCallbackException("Callbacks cannot be null.");
        }

        writeWithNonNullCallbacks(message, onSuccess, onFailure);
    }

    private void readWithNonNullCallbacks(final Consumer<String> onSuccess, final Consumer<Throwable> onFailure)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        channel.read(buffer, 0L, null, this, new CompletionHandler<Integer, Client>() {
            @Override
            public void completed(final Integer bytesRead, final Client client)
            {
                if (bytesRead == -1) {
                    return;
                }

                onSuccess.accept(new String(getNonZeroBytes(buffer), UTF_8));

                readWithNonNullCallbacks(onSuccess, onFailure);
            }

            @Override
            public void failed(final Throwable throwable, final Client client)
            {
                onFailure.accept(throwable);
            }
        });
    }

    private void writeWithNonNullCallbacks(
            final String message,
            final Consumer<Integer> onSuccess,
            final Consumer<Throwable> onFailure
    )
    {
        final ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(UTF_8));
        channel.write(buffer, 0L, null, this, new CompletionHandler<Integer, Client>() {
            @Override
            public void completed(final Integer bytesWritten, final Client client)
            {
                onSuccess.accept(bytesWritten);
            }

            @Override
            public void failed(final Throwable throwable, final Client client)
            {
                onFailure.accept(throwable);
            }
        });
    }

    private byte[] getNonZeroBytes(final ByteBuffer buffer)
    {
        final byte[] actual = new byte[buffer.position()];
        System.arraycopy(buffer.array(), 0, actual, 0, buffer.position());
        return actual;
    }
}