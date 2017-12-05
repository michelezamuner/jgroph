package net.slc.jgroph.adapters.remoteconsole;

import com.github.javafaker.Faker;
import net.slc.jgroph.adapters.remoteconsole.router.Response;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ErrorPresenterTest
{
    private final Faker faker = new Faker();

    @Test
    public void errorMessageIsWrittenToTheResponse()
    {
        final String message = faker.lorem().sentence();
        final Response response = mock(Response.class);

        final ErrorPresenter presenter = new ErrorPresenter(response);
        presenter.show(message);

        verify(response).write(message);
    }
}