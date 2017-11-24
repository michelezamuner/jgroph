package net.slc.jgroph.adapters.remoteconsole;

import net.slc.jgroph.infrastructure.server.Client;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.*;
import java.net.Socket;

public class ITResource
{
    @Test
    public void requestedResourceIsCorrectlyRetrieved()
            throws IOException, InterruptedException
    {
        final Socket client = new Socket("localhost", 8000);
        final boolean autoFlush = true;
        final PrintWriter writer = new PrintWriter(client.getOutputStream(), autoFlush);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

        Thread.sleep(1);
        writer.println("GET /resources/1");
        assertEquals("1 - Title 1", reader.readLine());
    }
}