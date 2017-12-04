package net.slc.jgroph.adapters.remoteconsole.router;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.github.javafaker.Faker;
import net.slc.jgroph.adapters.remoteconsole.router.Response;
import net.slc.jgroph.infrastructure.server.Client;
import org.junit.Test;

import java.util.function.Consumer;

public class ResponseTest
{
    private final Faker faker = new Faker();

    @Test
    @SuppressWarnings("unchecked")
    public void writeIsDelegatedToTheClient()
    {
        final String responseString = faker.lorem().sentence();
        final Client client = mock(Client.class);

        final Response response = new Response(client);
        response.write(responseString);

        verify(client).write(eq(responseString), any(Consumer.class), any(Consumer.class));
    }
}