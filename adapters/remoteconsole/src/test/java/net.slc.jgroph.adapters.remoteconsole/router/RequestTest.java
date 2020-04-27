package net.slc.jgroph.adapters.remoteconsole.router;

import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;

public class RequestTest
{
    @Test
    public void canParseTheRequestString()
            throws InvalidRequestFormatException, UnsupportedMethodException
    {
        final String method = "GET";
        final String prefix = "/resources";
        final String path = "/1";
        final String requestString = method + " " + prefix + path;

        final Request request = new Request(requestString);

        assertSame(Method.valueOf(method), request.getMethod());
        assertEquals(prefix, request.getPrefix());
        assertEquals(path, request.getPath());
    }
}