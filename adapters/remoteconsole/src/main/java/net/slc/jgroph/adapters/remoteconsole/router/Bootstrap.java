package net.slc.jgroph.adapters.remoteconsole.router;

import net.slc.jgroph.infrastructure.container.Container;
import net.slc.jgroph.infrastructure.server.Server;
import net.slc.jgroph.providers.Application;

import java.io.IOException;

public class Bootstrap
{
    private final Container container;
    private final Application application;

    public static void main(final String[] args)
            throws IOException, InterruptedException
    {
        if (args.length < 2) {
            throw new IllegalArgumentException("Server host and port are required.");
        }

        (new Bootstrap(new Container(), new Application())).execute(args[0], Integer.parseInt(args[1]));
        Thread.currentThread().join(0L);
    }

    Bootstrap(final Container container, final Application application)
    {
        this.container = container;
        this.application = application;
    }

    void execute(final String host, final int port)
            throws IOException
    {
        application.bootstrap(container);
        final Router router = container.make(Router.class);

        container.make(Server.class).listen(host, port, client -> {
            final Response response = new Response(client);
            client.read(message -> {
                try {
                    final Request request = new Request(message);
                    router.route(request, response);
                } catch (UnsupportedMethodException | InvalidRequestFormatException | IOException e) {
                    // TODO: return a proper response instead
                    e.printStackTrace(System.out);
                }
            }, t -> {});
        });
    }
}