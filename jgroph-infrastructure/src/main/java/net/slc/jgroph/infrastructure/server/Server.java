package net.slc.jgroph.infrastructure.server;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;
import java.net.InetSocketAddress;
import java.io.IOException;

public class Server
{
    private final AsynchronousServerSocketChannel channel;

    public Server(final AsynchronousServerSocketChannel channel)
    {
        this.channel = channel;
    }

    public void listen(final String host, final int port, final Consumer<Client> callback)
            throws IOException
    {
        channel.bind(new InetSocketAddress(host, port));
        channel.accept(this, new CompletionHandler<AsynchronousSocketChannel, Server>() {
            @Override
            public void completed(final AsynchronousSocketChannel channel, final Server server)
            {

            }

            @Override
            public void failed(final Throwable throwable, final Server server)
            {

            }
        });
    }
}