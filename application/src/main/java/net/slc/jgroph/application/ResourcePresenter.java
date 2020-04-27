package net.slc.jgroph.application;

import java.io.IOException;

public interface ResourcePresenter
{
    void show(final ResourceData data)
            throws IOException;
}