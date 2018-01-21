package net.slc.jgroph.adapters.api;

import com.github.javafaker.Faker;
import net.slc.jgroph.domain.InvalidResourceIdFormatException;
import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.domain.ResourceId;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class ApiResourcePresenterTest
{
    private final Faker faker = new Faker();
    @Rule public final TestOutputRule output = new TestOutputRule();
    @Mock private HttpServletResponse response;
    @InjectMocks private ApiResourcePresenter presenter;

    @Test
    public void showMethodProperlyUpdatesResponse()
            throws InvalidResourceIdFormatException, IOException
    {
        final int id = (int)faker.number().randomNumber();
        final String title = faker.book().title();
        final String json = String.format("{\n  \"id\": %d,\n  \"title\": \"%s\"\n}", id, title);
        final ResourceData resource = new ResourceData(new ResourceId(String.valueOf(id)), title);

        when(response.getWriter()).thenReturn(output.getWriter());

        presenter.show(resource);

        verify(response).setStatus(eq(200));
        verify(response).setHeader(eq("Content-Type"), eq("application/json"));
        output.assertOutputEquals(json);
    }
}