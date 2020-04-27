package net.slc.jgroph.bookmarks_services.adapters.http;

import com.github.javafaker.Faker;
import net.slc.jgroph.infrastructure.container.Container;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("initialization")
public class RouterTest
{
    private final Faker faker = new Faker();
    @Rule public final ExpectedException expectedException = ExpectedException.none();
    @Mock private HttpServletRequest servletRequest;
    @Mock private HttpServletResponse servletResponse;
    @Mock private Request request;
    @Mock private Response response;
    @Mock private Container container;
    @Mock private BookmarksController bookmarksController;
    private Router router;

    @Before
    public void setUp()
    {
        when(container.make(Request.class, servletRequest)).thenReturn(request);
        when(container.make(Response.class, servletResponse)).thenReturn(response);
        when(container.make(BookmarksController.class)).thenReturn(bookmarksController);

        router = new Router(container);
    }

    @Test
    public void supportsDefaultConstructor()
    {
        final Router router = new Router();
        Assert.assertNotNull(router);
    }

    @Test
    public void supportsOnlyBookmarksRoute()
            throws ServletException, IOException
    {
        final String path = faker.lorem().word();
        when(request.getPath()).thenReturn(path);

        if (!path.equals("/bookmarks")) {
            expectedException.expect(UnsupportedOperationException.class);
        }

        router.doGet(servletRequest, servletResponse);
    }

    @Test
    public void routesToIndexBookmarks()
           throws ServletException, IOException
    {
        when(request.getPath()).thenReturn("/bookmarks");
        router.doGet(servletRequest, servletResponse);
        verify(bookmarksController).index(request, response);
    }
}