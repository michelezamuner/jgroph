package net.slc.jgroph.adapters.remoteconsole;

import net.slc.jgroph.infrastructure.server.Server;

import java.io.IOException;

public class Bootstrap
{
    private final Server server;

    public static void main(final String[] args)
            throws IOException, InterruptedException
    {
        (new Bootstrap(new Server())).execute("0.0.0.0", 8000); //NOPMD
        Thread.currentThread().join(10_000);
    }

    Bootstrap(final Server server)
    {
        this.server = server;
    }

    void execute(final String host, final int port)
            throws IOException
    {
        server.listen(host, port, client ->
            client.write("Hello, World!", b -> {}, t -> {})
        );
    }
}