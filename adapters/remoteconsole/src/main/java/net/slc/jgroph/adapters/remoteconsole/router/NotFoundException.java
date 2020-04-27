package net.slc.jgroph.adapters.remoteconsole.router;

public class NotFoundException extends Exception
{
    public NotFoundException(final String message)
    {
        super(message);
    }

    public NotFoundException(final String message, final Throwable previous)
    {
        super(message, previous);
    }
}