package net.slc.jgroph.infrastructure.server;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

class ClientHandlersFactory
{
    CompletionHandler<Integer, Reader> createReadHandler(
            final ByteBuffer buffer,
            final Consumer<String> success,
            final Consumer<Throwable> failure
    )
    {
        return new ReadCompletionHandler(buffer, success, failure);
    }

    CompletionHandler<Integer, Client> createWriteHandler(
            final Consumer<Integer> success,
            final Consumer<Throwable> failure
    )
    {
        return new WriteCompletionHandler(success, failure);
    }
}