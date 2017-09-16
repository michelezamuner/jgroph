package net.slc.jgroph.adapters.web;

import com.github.javafaker.Faker;
import net.slc.jgroph.application.InvalidResourceIdFormatException;
import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.domain.ResourceId;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;

public class ResourcePresenterTest
{
    private Faker faker;

    @Before
    public void setUp()
    {
        this.faker = new Faker();
    }

    @Test
    public void showMethodProperlyUpdatesResponse()
            throws InvalidResourceIdFormatException, IOException
    {
        final int id = (int)this.faker.number().randomNumber();
        final String title = this.faker.book().title();
        final String json = String.format("{\n  \"id\": %d,\n  \"title\": \"%s\"\n}", id, title);
        final ResourceData resource = new ResourceData(new ResourceId(String.valueOf(id)), title);

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(new OutputStreamWriter(output));
        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(writer);

        final ResourcePresenter presenter = new ResourcePresenter(response);
        presenter.show(resource);

        verify(response).setStatus(eq(200));
        verify(response).setHeader(eq("Content-Type"), eq("application/json"));

        writer.flush();
        assertEquals(json, new String(output.toByteArray(), "UTF-8"));
    }
}