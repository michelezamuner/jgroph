package net.slc.jgroph.adapters.web;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ErrorPresenterTest
{
    private Faker faker;

    @Before
    public void setUp()
    {
        this.faker = new Faker();
    }

    @Test
    public void failMethodProperlyUpdatesResponse()
            throws IOException
    {
        final int status = (int)this.faker.number().randomNumber();
        final String message = this.faker.lorem().sentence();
        final String json = String.format("{\n  \"error\": \"%s\"\n}", message);

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(new OutputStreamWriter(output));
        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(writer);

        final ErrorPresenter presenter = new ErrorPresenter(response);
        presenter.fail(status, message);

        verify(response).setStatus(status);
        verify(response).setHeader(eq("Content-Type"), eq("application/json"));

        writer.flush();
        assertEquals(json, new String(output.toByteArray(), "UTF-8"));
    }
}