package net.slc.jgroph.adapters.remoteconsole.router;

import net.slc.jgroph.infrastructure.server.Client;

public class Response
{
    private final Client client;

    public Response(final Client client)
    {
        this.client = client;
    }

    public void write(final String message)
    {
        client.write(message, b -> {}, t -> {});
    }
}