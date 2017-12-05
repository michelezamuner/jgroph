package net.slc.jgroph.adapters.remoteconsole.router;

public class InvalidRequestFormatException extends Exception
{
    public InvalidRequestFormatException(final String message)
    {
        super(message);
    }

    public InvalidRequestFormatException(final String message, final Throwable previous)
    {
        super(message, previous);
    }
}