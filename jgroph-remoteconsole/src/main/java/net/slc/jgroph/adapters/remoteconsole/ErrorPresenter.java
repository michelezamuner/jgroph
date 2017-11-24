package net.slc.jgroph.adapters.remoteconsole;

import net.slc.jgroph.adapters.remoteconsole.router.Response;

public class ErrorPresenter
{
    private final Response response;

    public ErrorPresenter(final Response response)
    {
        this.response = response;
    }

    public void show(final String message)
    {
        response.write(message);
    }
}