package net.slc.jgroph.adapters.api;

import com.github.javafaker.Faker;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
public class ErrorPresenterTest
{
    private final Faker faker = new Faker();
    @Mock private HttpServletResponse response;
    @Rule public final TestOutputRule output = new TestOutputRule();

    @Test
    public void failMethodProperlyUpdatesResponse()
            throws IOException
    {
        final int status = (int)this.faker.number().randomNumber();
        final String message = this.faker.lorem().sentence();
        final String json = String.format("{\n  \"error\": \"%s\"\n}", message);

        when(response.getWriter()).thenReturn(output.getWriter());

        final ErrorPresenter presenter = new ErrorPresenter(response);
        presenter.fail(status, message);

        verify(response).setStatus(status);
        verify(response).setHeader(eq("Content-Type"), eq("application/json"));
        output.assertOutputEquals(json);
    }
}