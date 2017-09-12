package net.slc.jgroph.adapters.web;

import net.slc.jgroph.application.ResourcePresenter;

import javax.servlet.http.HttpServletResponse;

public class PresenterFactory
{
    public ResourcePresenter createResourcePresenter(final HttpServletResponse response)
    {
        return null;
    }

    public ErrorPresenter createErrorPresenter(final HttpServletResponse response)
    {
        return null;
    }
}