package net.slc.jgroph.adapters.remoteconsole;

import net.slc.jgroph.infrastructure.server.Client;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;

public class ITResource
{
    @Test
    public void testRequestedResourceIsCorrectlyRetrieved()
            throws IOException
    {
        final Socket client = new Socket("localhost", 8000);
        final Reader reader = new InputStreamReader(client.getInputStream());
        assertEquals("Hello, World!", readMessage(reader));
    }

    private String readMessage(final Reader reader)
            throws IOException
    {
        final StringBuilder message = new StringBuilder();
        final char[] buffer = new char[Client.BUFFER_SIZE];
        final int bytesRead = reader.read(buffer);
        message.append(buffer, 0, bytesRead);

        return message.toString();
    }
}