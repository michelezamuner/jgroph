package net.slc.jgroph.infrastructure.server;

import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

class WriteCompletionHandler implements CompletionHandler<Integer, Client>
{
    private final Consumer<Integer> success;
    private final Consumer<Throwable> failure;

    WriteCompletionHandler(final Consumer<Integer> success, final Consumer<Throwable> failure)
    {
        this.success = success;
        this.failure = failure;
    }

    @Override
    public void completed(final Integer bytesWritten, final Client client)
    {

    }

    @Override
    public void failed(final Throwable throwable, final Client client)
    {

    }
}