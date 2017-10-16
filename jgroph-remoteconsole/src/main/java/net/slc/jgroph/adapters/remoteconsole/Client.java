package net.slc.jgroph.adapters.remoteconsole;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Client
{
    private final AsynchronousSocketChannel client;

    public Client(final AsynchronousSocketChannel client)
    {
        this.client = client;
    }

    public void read(final Consumer<String> success, final Consumer<Throwable> failure)
    {
        final ByteBuffer buffer = ByteBuffer.allocate(2048);
        client.read(buffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer bytesRead, Object o) {
                if (bytesRead == -1) {
                    return;
                }

                success.accept(new String(buffer.array(), UTF_8));
                read(success, failure);
            }

            @Override
            public void failed(Throwable throwable, Object o) {
                if (failure != null) {
                    failure.accept(throwable);
                }
            }
        });
    }

    public void write(final String message, final Consumer<Integer> success, final Consumer<Throwable> failure)
    {
        final ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(UTF_8));
        client.write(buffer, null, new CompletionHandler<Integer, Object>() {
            @Override
            public void completed(Integer bytesWritten, Object o) {
                if (success != null) {
                    success.accept(bytesWritten);
                }
            }

            @Override
            public void failed(Throwable throwable, Object o) {
                if (failure != null) {
                    failure.accept(throwable);
                }
            }
        });
    }
}