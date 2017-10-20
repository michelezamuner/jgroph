package net.slc.jgroph.infrastructure.server;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;
import java.nio.ByteBuffer;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.List;
import java.util.ArrayList;

public class Client
{
    private final AsynchronousSocketChannel channel;

    public Client(final AsynchronousSocketChannel channel)
    {
        this.channel = channel;
    }

    public void read(final Consumer<String> callback)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(2048);
        channel.read(buffer, 0L, null, this, new CompletionHandler<Integer, Client>() {
            @Override
            public void completed(final Integer bytesRead, final Client client)
            {
                final byte[] actual = new byte[buffer.position()];
                System.arraycopy(buffer.array(), 0, actual, 0, buffer.position());
                callback.accept(new String(actual, UTF_8));
            }

            @Override
            public void failed(final Throwable throwable, final Client client)
            {

            }
        });
    }

    public void write(final String message)
    {
        final ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(UTF_8));
        channel.write(buffer, 0L, null, this, new CompletionHandler<Integer, Client>() {
            @Override
            public void completed(final Integer bytesWritten, final Client client)
            {

            }

            @Override
            public void failed(final Throwable throwable, final Client client)
            {

            }
        });
    }
}