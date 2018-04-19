package net.slc.jgroph.bookmarks_services.adapters.http;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@SuppressWarnings("initialization")
@RunWith(MockitoJUnitRunner.class)
public class BookmarksControllerTest
{
    @Mock private Request request;
    @Mock private Response response;
    private BookmarksController controller;

    @Before
    public void setUp()
    {
        controller = new BookmarksController();
    }

    @Test
    public void indexWritesExpectedContents()
            throws ResponseException
    {
        controller.index(request, response);
        Mockito.verify(response).setJsonContentType();
        Mockito.verify(response).write("[\n  {\n    \"id\": 1,\n    \"title\": \"Title 1\"\n  },\n"
                + "  {\n    \"id\": 2,\n    \"title\": \"Title 2\"\n  }\n]");
    }
}