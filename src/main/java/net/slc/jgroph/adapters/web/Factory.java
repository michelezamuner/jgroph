package net.slc.jgroph.adapters.web;

import net.slc.jgroph.application.ResourcePresenter;
import net.slc.jgroph.application.ShowResource;

import javax.servlet.http.HttpServletResponse;

public class Factory
{
    public ShowResource createShowResource(final ResourcePresenter presenter)
    {
        return null;
    }

    public ResourcePresenter createResourcePresenter(final HttpServletResponse response)
    {
        return null;
    }

    public ErrorPresenter createErrorPresenter(final HttpServletResponse response)
    {
        return null;
    }
}