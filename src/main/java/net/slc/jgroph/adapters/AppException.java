package net.slc.jgroph.adapters;

public class AppException extends Exception
{
    public AppException(final String message)
    {
        super(message);
    }

    public AppException(final String message, final Throwable previous)
    {
        super(message, previous);
    }
}