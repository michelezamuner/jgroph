package net.slc.jgroph.adapters.remoteconsole.router;

import net.slc.jgroph.adapters.remoteconsole.router.InvalidRequestFormatException;
import net.slc.jgroph.adapters.remoteconsole.router.Method;
import net.slc.jgroph.adapters.remoteconsole.router.Request;
import net.slc.jgroph.adapters.remoteconsole.router.UnsupportedMethodException;
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

    @Test(expected = UnsupportedMethodException.class)
    public void failsIfMethodIsNotSupported()
            throws InvalidRequestFormatException, UnsupportedMethodException
    {
        new Request("UNSUPPORTED /resources/1");
    }

    @Test(expected = InvalidRequestFormatException.class)
    public void failsIfRequestPathDoesNotStartWithASlash()
            throws InvalidRequestFormatException, UnsupportedMethodException
    {
        new Request("GET not/starting/with/slash");
    }

    @Test(expected = InvalidRequestFormatException.class)
    public void failsIfRequestHasTooFewElements()
            throws InvalidRequestFormatException, UnsupportedMethodException
    {
        new Request("GET");
    }

    @Test(expected = InvalidRequestFormatException.class)
    public void failsIfRequestHasTooManyElements()
            throws InvalidRequestFormatException, UnsupportedMethodException
    {
        new Request("GET /path body too many elements");
    }
}