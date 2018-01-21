package net.slc.jgroph.infrastructure.server;

import com.github.javafaker.Faker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;

public class ITServer
{
    private class Status
    {
        boolean connected = false;
        String message;
        Throwable exception;
    }

    private final int SLEEP_TIME = 1000;
    private final Faker faker = new Faker();
    private Status status;
    private Socket client;
    private int port;

    @Before
    public void setUp()
    {
        status = new Status();
    }

    @After
    public void tearDown()
            throws IOException
    {
        client.close();
    }

    @Test
    public void ICanConnectToTheServer()
            throws InterruptedException, IOException
    {
        port = 8000;
        startServer(client -> status.connected = true);
        connect(port);

        assertNull(status.exception);
        assertTrue(status.connected);
    }

    @Test
    public void ICanReadAMessageFromClient()
            throws InterruptedException, IOException
    {
        port = 8001;
        startServer(client -> client.read(message -> status.message = message, e -> status.exception = e));
        connect(port);

        final String message = faker.lorem().sentence();
        writeMessage(new OutputStreamWriter(client.getOutputStream(), UTF_8), message);

        assertNull(status.exception);
        assertEquals(message, status.message);
    }

    @Test
    public void ICanReadMultipleMessagesFromClient()
            throws InterruptedException, IOException
    {
        port = 8002;
        startServer(client -> client.read(message -> status.message = message, e -> status.exception = e));
        connect(port);
        final Writer writer = new OutputStreamWriter(client.getOutputStream(), UTF_8);

        final String firstMessage = faker.lorem().sentence();
        writeMessage(writer, firstMessage);

        assertNull(status.exception);
        assertEquals(firstMessage, status.message);

        final String secondMessage = faker.lorem().sentence();
        writeMessage(writer, secondMessage);

        assertNull(status.exception);
        assertEquals(secondMessage, status.message);
    }

    @Test
    public void ICanWriteAMessageToClient()
            throws InterruptedException, IOException
    {
        port = 8003;
        final String message = faker.lorem().sentence();
        startServer(client -> client.write(message, i -> {}, e -> status.exception = e));
        connect(port);
        final Reader reader = new InputStreamReader(client.getInputStream());

        assertNull(status.exception);
        assertEquals(message + '\n', readMessage(reader));
    }

    private void startServer(final Consumer<Client> callback)
            throws InterruptedException
    {
        new Thread(() -> {
            try {
                new Server().listen("0.0.0.0", port, callback);
            } catch (IOException e) {
                status.exception = e;
            }
        }).start();

        // Leave time for server to start before assuming it can receive connections
        Thread.sleep(SLEEP_TIME);
    }

    private void connect(final int port)
            throws IOException, InterruptedException
    {
        client = new Socket("localhost", port);

        // Leave time for server to accept the connection before assuming it can be used
        Thread.sleep(SLEEP_TIME);
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

    private void writeMessage(final Writer writer, final String message)
            throws IOException, InterruptedException
    {
        writer.write(message);
        writer.flush();

        // Leave time for server to receive the message before assuming it has been handled
        Thread.sleep(SLEEP_TIME);
    }
}