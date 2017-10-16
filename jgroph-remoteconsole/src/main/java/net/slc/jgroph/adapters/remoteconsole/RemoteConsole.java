package net.slc.jgroph.adapters.remoteconsole;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.function.Consumer;

public class RemoteConsole
{
    public static void main(final String[] args)
            throws IOException, InterruptedException
    {
        final RemoteConsole server = new RemoteConsole();

        server.listen("0.0.0.0", 8000, client -> {
            client.read(message -> {
                System.out.println(message);
                client.write("You wrote: " + message, null, null);
            }, null);
        });

        Thread.currentThread().join();
    }

    private void listen(final String host, final int port, final Consumer<Client> callback)
            throws IOException
    {
        final InetSocketAddress address = new InetSocketAddress(host, port);
        final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open().bind(address);
        accept(server, callback);
    }

    private void accept(final AsynchronousServerSocketChannel server, final Consumer<Client> callback)
    {
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel client, Object o) {
                System.out.println("NEW CONNECTION");
                callback.accept(new Client(client));
                accept(server, callback);
            }

            @Override
            public void failed(Throwable e, Object o) {
                System.out.println(e.getMessage());
            }
        });
    }
}