package net.slc.jgroph.infrastructure.server;

import java.nio.channels.AsynchronousSocketChannel;

public class ClientFactory
{
    public Client create(final AsynchronousSocketChannel channel)
    {
        return new Client(channel);
    }
}