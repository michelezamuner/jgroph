package net.slc.jgroph.infrastructure.container;

public class ContainerError extends Error
{
    public ContainerError(final String message)
    {
        super(message);
    }

    public ContainerError(final String message, final Throwable previous)
    {
        super(message, previous);
    }
}