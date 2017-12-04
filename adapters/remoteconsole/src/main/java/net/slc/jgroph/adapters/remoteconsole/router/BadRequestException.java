package net.slc.jgroph.adapters.remoteconsole.router;

public class BadRequestException extends Exception
{
    public BadRequestException(final String message)
    {
        super(message);
    }

    public BadRequestException(final String message, final Throwable previous)
    {
        super(message, previous);
    }
}