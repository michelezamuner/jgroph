package net.slc.jgroph.adapters.api;

import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

public class BasePresenterTest
{
    @Test
    public void doesNotEscapeCharacters()
            throws IOException
    {
        final String message = "Message 'with' <HTML> char=acters";

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(new OutputStreamWriter(output));
        final HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(writer);

        final PresenterDouble presenter = new PresenterDouble(response);
        presenter.render(message);
        writer.flush();

        final String json = String.format("{\n  \"message\": \"%s\"\n}", message);
        assertEquals(json ,new String(output.toByteArray(), "UTF-8"));
    }
}