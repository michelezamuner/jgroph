package net.slc.jgroph.adapters.web;

import net.slc.jgroph.application.ResourcePresenter;
import net.slc.jgroph.application.ShowResource;

import javax.servlet.http.HttpServletResponse;

public class Factory
{
    public ShowResource createShowResource(ResourcePresenter presenter)
    {
        return new ShowResource();
    }

    public ResourcePresenter createResourcePresenter(HttpServletResponse response)
    {
        return null;
    }
}