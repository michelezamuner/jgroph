package net.slc.jgroph.adapters.remoteconsole;

import net.slc.jgroph.adapters.remoteconsole.router.Response;
import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.application.ResourcePresenter;

import java.io.IOException;

public class ConsoleResourcePresenter implements ResourcePresenter
{
    private final Response response;

    public ConsoleResourcePresenter(final Response response)
    {
        this.response = response;
    }

    public void show(final ResourceData data)
            throws IOException
    {
        response.write(String.format("%s - %s", data.getId().toInt(), data.getTitle()));
    }
}