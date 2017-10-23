package net.slc.jgroph.infrastructure.server;

import com.github.javafaker.Faker;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.net.Socket;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ITServer
{
    private final Faker faker = new Faker();
    private volatile Throwable exception;

    @Test
    public void ICanConnectToTheServer()
            throws Throwable
    {
        final int port = 8000;

        class Status
        {
            public boolean connected = false;
        }

        final Status status = new Status();
        final Thread thread = new Thread(() -> {
            try {
                final Server server = new Server();
                server.listen("0.0.0.0", port, client -> status.connected = true);
            } catch (IOException e) {
                exception = e;
            }

        });
        thread.run();

        final Socket client = new Socket("localhost", port);

        Thread.sleep(1000);

        if (exception != null) {
            throw exception;
        }
        assertTrue(status.connected);

        client.close();
        thread.join();
    }

    @Test
    public void ICanReadAMessageFromClient()
            throws Throwable
    {
        final int port = 8001;

        class Status
        {
            public String message;
        }

        final Status status = new Status();
        final Thread thread = new Thread(() -> {
            try {
                final Server server = new Server();
                server.listen("0.0.0.0", port, client ->
                    client.read(message -> status.message = message, e -> exception = e)
                );
            } catch (IOException e) {
                exception = e;
            }
        });

        thread.run();

        final String message = faker.lorem().sentence();
        final Socket client = new Socket("localhost", port);
        final Writer writer = new OutputStreamWriter(client.getOutputStream(), UTF_8);
        writer.write(message);
        writer.flush();

        Thread.sleep(1000);

        if (exception != null) {
            throw exception;
        }
        assertEquals(message, status.message);

        client.close();
        thread.join();
    }

    @Test
    public void ICanReadMultipleMessagesFromClient()
            throws Throwable
    {
        final int port = 8002;

        class Status
        {
            public String message;
        }

        final Status status = new Status();
        final Thread thread = new Thread(() -> {
            try {
                final Server server = new Server();
                server.listen("0.0.0.0", port, client -> {
                    client.read(message -> status.message = message, e -> exception = e);
                });
            } catch (IOException e) {
                exception = e;
            }
        });

        thread.run();

        final Socket client = new Socket("localhost", port);
        final Writer writer = new OutputStreamWriter(client.getOutputStream(), UTF_8);

        final String firstMessage = faker.lorem().sentence();
        writer.write(firstMessage);
        writer.flush();

        Thread.sleep(1000);

        if (exception != null) {
            throw exception;
        }
        assertEquals(firstMessage, status.message);

        final String secondMessage = faker.lorem().sentence();
        writer.write(secondMessage);
        writer.flush();

        Thread.sleep(1000);

        if (exception != null) {
            throw exception;
        }
        assertEquals(secondMessage, status.message);

        thread.join();
    }

    @Test
    public void ICanWriteAMessageToClient()
            throws Throwable
    {
        final int port = 8003;
        final String message = faker.lorem().sentence();

        final Thread thread = new Thread(() -> {
            try {
                final Server server = new Server();
                server.listen("0.0.0.0", port, client ->
                        client.write(message, i -> {}, e -> exception = e)
                );
            } catch (IOException e) {
                exception = e;
            }
        });

        thread.run();

        final Socket client = new Socket("localhost", port);

        Thread.sleep(1000);

        final Reader reader = new InputStreamReader(client.getInputStream());

        if (exception != null) {
            throw exception;
        }
        assertEquals(message, readMessage(reader));

        thread.join();
    }

    private String readMessage(final Reader reader)
            throws IOException
    {
        final StringBuilder string = new StringBuilder();
        final char[] buffer = new char[Client.BUFFER_SIZE];
        final int bytesRead = reader.read(buffer);
        string.append(buffer, 0, bytesRead);

        return string.toString();
    }
}