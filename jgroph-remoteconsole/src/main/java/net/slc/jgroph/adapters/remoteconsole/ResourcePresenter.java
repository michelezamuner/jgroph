package net.slc.jgroph.adapters.remoteconsole;

import net.slc.jgroph.adapters.remoteconsole.router.Response;
import net.slc.jgroph.application.ResourceData;

import java.io.IOException;

public class ResourcePresenter implements net.slc.jgroph.application.ResourcePresenter
{
    private final Response response;

    public ResourcePresenter(final Response response)
    {
        this.response = response;
    }

    public void show(final ResourceData data)
            throws IOException
    {
        response.write(String.format("%s - %s", data.getId().toInt(), data.getTitle()));
    }
}