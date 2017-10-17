package net.slc.jgroph.infrastructure.server;

import org.junit.Test;

import java.nio.channels.AsynchronousServerSocketChannel;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServerTest
{
    @Test
    public void channelIsUsedWithGivenHandler()
    {
        final AsynchronousServerSocketChannel channel = mock(AsynchronousServerSocketChannel.class);
        final AcceptCompletionHandler handler = mock(AcceptCompletionHandler.class);

        final Server server = new Server(channel, handler);
        server.accept();

        verify(channel).accept(eq(server), eq(handler));
    }
}