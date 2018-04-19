package net.slc.jgroph.bookmarks_services.adapters.http;

import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("initialization")
@RunWith(MockitoJUnitRunner.class)
public class RequestTest
{
    private final Faker faker = new Faker();
    @Mock private HttpServletRequest servletRequest;
    private Request request;

    @Before
    public void setUp()
    {
        request = new Request(servletRequest);
    }

    @Test
    public void getsCorrectPath()
    {
        final String path = faker.lorem().word();
        when(servletRequest.getRequestURI()).thenReturn(path);
        assertEquals(path, request.getPath());
    }
}