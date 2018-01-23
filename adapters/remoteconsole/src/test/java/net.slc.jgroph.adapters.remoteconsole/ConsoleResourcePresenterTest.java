package net.slc.jgroph.adapters.remoteconsole;

import com.github.javafaker.Faker;
import net.slc.jgroph.adapters.remoteconsole.router.Response;
import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.domain.ResourceId;
import net.slc.jgroph.domain.InvalidResourceIdFormatException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("initialization")
public class ConsoleResourcePresenterTest
{
    private final Faker faker = new Faker();
    @Mock private Response response;
    @InjectMocks private ConsoleResourcePresenter presenter;

    @Test
    public void properMessageIsWrittenToResponseOnShow()
            throws InvalidResourceIdFormatException, IOException
    {
        final String resourceId = String.valueOf(faker.number().randomNumber());
        final String title = faker.book().title();
        final ResourceData data = new ResourceData(new ResourceId(resourceId), title);

        presenter.show(data);

        verify(response).write(String.format("%s - %s", resourceId, title));
    }
}