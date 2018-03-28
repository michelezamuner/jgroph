package net.slc.jgroph.bookmarks_services.adapters.http;

public class ResponseException extends RuntimeException
{
    public ResponseException(final String message)
    {
        super(message);
    }

    public ResponseException(final String message, final Throwable previous)
    {
        super(message, previous);
    }
}