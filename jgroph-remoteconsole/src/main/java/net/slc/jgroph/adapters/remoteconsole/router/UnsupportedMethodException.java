package net.slc.jgroph.adapters.remoteconsole.router;

public class UnsupportedMethodException extends Exception
{
    UnsupportedMethodException(final String message)
    {
        super(message);
    }

    UnsupportedMethodException(final String message, final Throwable previous)
    {
        super(message, previous);
    }
}