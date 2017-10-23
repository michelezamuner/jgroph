package net.slc.jgroph.infrastructure.server;

public class MissingCallbackError extends Error
{
    public MissingCallbackError(final String message)
    {
        super(message);
    }

    public MissingCallbackError(final String message, final Throwable previous)
    {
        super(message, previous);
    }
}