package net.slc.jgroph.infrastructure.server;

import java.nio.channels.AsynchronousSocketChannel;

class ClientFactory
{
    private final BufferFactory bufferFactory;
    private final ClientHandlersFactory handlersFactory;

    public ClientFactory(final BufferFactory bufferFactory, final ClientHandlersFactory handlersFactory)
    {
        this.bufferFactory = bufferFactory;
        this.handlersFactory = handlersFactory;
    }

    public Client create(final AsynchronousSocketChannel channel)
    {
        return new Client(channel, bufferFactory, handlersFactory);
    }
}