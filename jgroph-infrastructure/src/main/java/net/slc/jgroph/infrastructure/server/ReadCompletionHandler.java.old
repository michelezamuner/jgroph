package net.slc.jgroph.infrastructure.server;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

class ReadCompletionHandler implements CompletionHandler<Integer, Reader>
{
    private final ByteBuffer buffer;
    private final Consumer<String> success;
    private final Consumer<Throwable> failure;

    ReadCompletionHandler(final ByteBuffer buffer, final Consumer<String> success, final Consumer<Throwable> failure)
    {
        this.buffer = buffer;
        this.success = success;
        this.failure = failure;
    }

    @Override
    public void completed(final Integer bytesRead, final Reader reader)
    {
        if (bytesRead == -1) {
            return;
        }

        final String message = new String(buffer.array(), BufferFactory.DEFAULT_CHARSET);
        success.accept(message);
        reader.accept(success, failure);
    }

    @Override
    public void failed(final Throwable throwable, final Reader reader)
    {
        failure.accept(throwable);
    }
}