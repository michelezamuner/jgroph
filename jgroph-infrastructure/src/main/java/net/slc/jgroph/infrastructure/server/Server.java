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
    private final ClientFactory clientFactory;

    Server(final AsynchronousServerSocketChannel channel, final ClientFactory clientFactory)
    {
        this.channel = channel;
        this.clientFactory = clientFactory;
    }

    public Server(final AsynchronousServerSocketChannel channel)
    {
        this(channel, new ClientFactory());
    }

    public void listen(final String host, final int port, final Consumer<Client> callback)
            throws IOException, MissingCallbackException
    {
        if (callback == null) {
            throw new MissingCallbackException("Missing required callback.");
        }

        channel.bind(new InetSocketAddress(host, port));
        accept(callback);
    }

    private void accept(final Consumer<Client> callback)
    {
        channel.accept(this, new CompletionHandler<AsynchronousSocketChannel, Server>() {
            @Override
            public void completed(final AsynchronousSocketChannel channel, final Server server)
            {
                callback.accept(clientFactory.create(channel));
                accept(callback);
            }

            @Override
            public void failed(final Throwable throwable, final Server server)
            {

            }
        });
    }
}