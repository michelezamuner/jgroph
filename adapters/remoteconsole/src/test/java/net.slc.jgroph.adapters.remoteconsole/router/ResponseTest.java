package net.slc.jgroph.adapters.remoteconsole.router;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.github.javafaker.Faker;
import net.slc.jgroph.infrastructure.server.Client;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.function.Consumer;

@RunWith(MockitoJUnitRunner.class)
public class ResponseTest
{
    private final Faker faker = new Faker();
    @Mock private Client client;
    @InjectMocks private Response response;

    @Test
    @SuppressWarnings("unchecked")
    public void writeIsDelegatedToTheClient()
    {
        final String responseString = faker.lorem().sentence();
        response.write(responseString);
        verify(client).write(eq(responseString), any(Consumer.class), any(Consumer.class));
    }
}