package net.slc.jgroph.adapters.remoteconsole.router;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.runners.Parameterized.Parameters;
import static org.junit.runners.Parameterized.Parameter;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class RequestExceptionsTest
{
    @Parameters
    public static Collection<Object[]> getData()
    {
        return Arrays.asList(new Object[][]{
                { "UNSUPPORTED /resources/1", new UnsupportedMethodException("Method UNSUPPORTED is not supported.") },
                { "GET not/starting/with/slash", new InvalidRequestFormatException("Request path must start with a slash.") },
                { "GET", new InvalidRequestFormatException("Invalid request format.") },
                { "GET /path body too many elements", new InvalidRequestFormatException("Invalid request format.") }
        });
    }

    @Rule public ExpectedException expectedException = ExpectedException.none();
    @Parameter public String requestString;
    @Parameter(1) public Exception actualException;

    @Test
    public void failsIfInvalidRequest()
            throws InvalidRequestFormatException, UnsupportedMethodException
    {
        expectedException.expect(actualException.getClass());
        expectedException.expectMessage(actualException.getMessage());
        new Request(requestString);
    }
}