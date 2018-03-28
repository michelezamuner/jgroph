package net.slc.jgroph.bookmarks_services.adapters.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@SuppressWarnings("initialization")
@RunWith(MockitoJUnitRunner.class)
public class BookmarksControllerTest
{
    @Mock private Request request;
    @Mock private Response response;
    @InjectMocks private BookmarksController controller;

    @Test
    public void indexWritesExpectedContents()
    {
        controller.index(request, response);
        Mockito.verify(response).setContentType(Response.CT_APPLICATION_JSON);
        Mockito.verify(response).write("[\n  {\n    \"id\": 1,\n    \"title\": \"Title 1\"\n  },\n"
                + "  {\n    \"id\": 2,\n    \"title\": \"Title 2\"\n  }\n]");
    }
}