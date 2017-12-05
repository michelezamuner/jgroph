package net.slc.jgroph.domain;

public class InvalidResourceIdFormatException extends Exception
{
    public InvalidResourceIdFormatException(final String message)
    {
        super(message);
    }

    public InvalidResourceIdFormatException(final String message, final Throwable previous)
    {
        super(message, previous);
    }
}