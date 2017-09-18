package net.slc.jgroph.infrastructure.container;

public class ContainerException extends Exception
{
    public ContainerException(final String message)
    {
        super(message);
    }

    public ContainerException(final String message, final Throwable previous)
    {
        super(message, previous);
    }
}