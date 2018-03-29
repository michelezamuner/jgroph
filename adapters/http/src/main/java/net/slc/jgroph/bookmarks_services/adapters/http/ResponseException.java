package net.slc.jgroph.bookmarks_services.adapters.http;

import java.io.IOException;

public class ResponseException extends IOException
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