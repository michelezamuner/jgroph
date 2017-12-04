package net.slc.jgroph.application;

public class ResourceNotFoundException extends Exception
{
    public ResourceNotFoundException(final String message)
    {
        super(message);
    }

    public ResourceNotFoundException(final String message, final Throwable previous)
    {
        super(message, previous);
    }
}