package net.slc.jgroph.adapters.remoteconsole;

import com.github.javafaker.Faker;
import net.slc.jgroph.adapters.remoteconsole.router.Response;
import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.domain.ResourceId;
import net.slc.jgroph.domain.InvalidResourceIdFormatException;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ResourcePresenterAdapterTest
{
    private final Faker faker = new Faker();

    @Test
    public void properMessageIsWrittenToResponseOnShow()
            throws InvalidResourceIdFormatException, IOException
    {
        final String resourceId = String.valueOf(faker.number().randomNumber());
        final String title = faker.book().title();

        final Response response = mock(Response.class);
        final ResourceData data = new ResourceData(new ResourceId(resourceId), title);

        final ResourcePresenterAdapter presenter = new ResourcePresenterAdapter(response);
        presenter.show(data);

        verify(response).write(String.format("%s - %s", resourceId, title));
    }
}