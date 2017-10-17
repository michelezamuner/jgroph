package net.slc.jgroph.infrastructure.server;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, Server>
{
    private final ClientFactory factory;
    private final Consumer<Client> callback;

    public AcceptCompletionHandler(final ClientFactory factory, final Consumer<Client> callback)
    {
        this.factory = factory;
        this.callback = callback;
    }

    @Override
    public void completed(final AsynchronousSocketChannel client, Server server)
    {
        callback.accept(factory.create(client));
        server.accept();
    }

    @Override
    public void failed(final Throwable e, Server server)
    {

    }
}