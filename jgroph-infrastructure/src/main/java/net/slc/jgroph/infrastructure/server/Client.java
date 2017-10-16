package net.slc.jgroph.infrastructure.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

public class Client
{
    private final AsynchronousSocketChannel channel;
    private final BufferFactory bufferFactory;
    private final ClientHandlersFactory handlersFactory;

    Client(
            final AsynchronousSocketChannel channel,
            final BufferFactory bufferFactory,
            final ClientHandlersFactory handlersFactory
    )
    {
        this.channel = channel;
        this.bufferFactory = bufferFactory;
        this.handlersFactory = handlersFactory;
    }

    public void read(final Consumer<String> success, final Consumer<Throwable> failure)
            throws MissingCallbackException
    {
        if (success == null || failure == null) {
            throw new MissingCallbackException("Callbacks must be provided.");
        }

        doRead(success, failure);
    }

    private void doRead(final Consumer<String> success, final Consumer<Throwable> failure)
    {
        final ByteBuffer buffer = bufferFactory.createForRead();
        final CompletionHandler<Integer, Reader> handler = handlersFactory.createReadHandler(buffer, success, failure);
        channel.read(buffer, this::doRead, handler);
    }

    public void write(final String message, final Consumer<Integer> success, final Consumer<Throwable> failure)
            throws MissingCallbackException
    {
        if (success == null || failure == null) {
            throw new MissingCallbackException("Callbacks must be provided.");
        }

        final ByteBuffer buffer = bufferFactory.createForWrite(message);
        final CompletionHandler<Integer, Client> handler = handlersFactory.createWriteHandler(success, failure);
        channel.write(buffer, this, handler);
    }
}