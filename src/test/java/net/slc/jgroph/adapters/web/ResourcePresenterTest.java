package net.slc.jgroph.adapters.web;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.slc.jgroph.application.InvalidResourceIdFormatException;
import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.domain.ResourceId;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
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
    public void showMethodOutputsCorrectJson()
            throws InvalidResourceIdFormatException, IOException
    {
        final String id = String.valueOf(this.faker.number().randomNumber());
        final String title = this.faker.book().title();
        final String expected = String.format("{\n  \"id\": \"%s\",\n  \"title\": \"%s\"\n}", id, title);
        final ResourceData resource = new ResourceData(new ResourceId(id), title);

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(new OutputStreamWriter(output));
        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(writer);

        final ResourcePresenter presenter = new ResourcePresenter(response);
        presenter.show(resource);
        writer.flush();
        assertEquals(expected, new String(output.toByteArray(), "UTF-8"));
    }
}