package net.slc.jgroph.adapters.remoteconsole;

import com.github.javafaker.Faker;
import net.slc.jgroph.adapters.remoteconsole.router.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("initialization")
public class ErrorPresenterTest
{
    private final Faker faker = new Faker();
    @Mock private Response response;
    @InjectMocks private ErrorPresenter presenter;

    @Test
    public void errorMessageIsWrittenToTheResponse()
    {
        final String message = faker.lorem().sentence();
        presenter.show(message);
        verify(response).write(message);
    }
}