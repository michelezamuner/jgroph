package net.slc.jgroph.infrastructure.server;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

class Server
{
    private final AsynchronousServerSocketChannel channel;
    private final CompletionHandler<AsynchronousSocketChannel, Server> handler;

    public Server(
            final AsynchronousServerSocketChannel channel,
            final CompletionHandler<AsynchronousSocketChannel, Server> handler
    )
    {
        this.channel = channel;
        this.handler = handler;
    }

    public void accept()
    {
        channel.accept(this, handler);
    }
}