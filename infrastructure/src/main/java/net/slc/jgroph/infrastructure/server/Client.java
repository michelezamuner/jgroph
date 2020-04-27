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
    {
        if (onSuccess == null || onFailure == null) {
            throw new MissingCallbackError("Callbacks cannot be null.");
        }

        final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        readFromChannel(buffer, new CompletionHandler<Integer, Client>() {
            @Override
            public void completed(final Integer bytesRead, final Client client)
            {
                if (bytesRead == -1) {
                    return;
                }

                onSuccess.accept(new String(getNonZeroBytes(buffer), UTF_8).trim());

                read(onSuccess, onFailure);
            }

            @Override
            public void failed(final Throwable throwable, final Client client)
            {
                onFailure.accept(throwable);
            }
        });
    }

    public void write(final String message, final Consumer<Integer> onSuccess, final Consumer<Throwable> onFailure)
    {
        if (onSuccess == null || onFailure == null) {
            throw new MissingCallbackError("Callbacks cannot be null.");
        }

        final ByteBuffer buffer = ByteBuffer.wrap((message + '\n').getBytes(UTF_8));
        writeToChannel(buffer, new WriteHandler(onSuccess, onFailure));
    }

    /**
     * The write handler doesn't depend on the invocation object, so for performance reasons it's better to define it
     * as a static inner class.
     *
     * @see http://findbugs.sourceforge.net/bugDescriptions.html#SIC_INNER_SHOULD_BE_STATIC_ANON
     */
    private static class WriteHandler implements CompletionHandler<Integer, Client>
    {
        private final Consumer<Integer> onSuccess;
        private final Consumer<Throwable> onFailure;

        WriteHandler(final Consumer<Integer> onSuccess, final Consumer<Throwable> onFailure)
        {
            this.onSuccess = onSuccess;
            this.onFailure = onFailure;
        }

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
    }

    @SuppressWarnings("nullness")
    private void readFromChannel(final ByteBuffer buffer, final CompletionHandler<Integer, Client> handler)
    {
        channel.read(buffer, 0L, null, this, handler);
    }

    @SuppressWarnings("nullness")
    private void writeToChannel(final ByteBuffer buffer, final WriteHandler handler)
    {
        channel.write(buffer, 0L, null, this, handler);
    }

    private byte[] getNonZeroBytes(final ByteBuffer buffer)
    {
        final byte[] actual = new byte[buffer.position()];
        System.arraycopy(buffer.array(), 0, actual, 0, buffer.position());
        return actual;
    }
}