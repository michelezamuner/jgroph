package net.slc.jgroph.bookmarks_services.adapters.http;

public class BookmarksController
{
    void index(final Request request, final Response response)
    {
        response.setContentType(Response.CT_APPLICATION_JSON);
        response.write("[\n  {\n    \"id\": 1,\n    \"title\": \"Title 1\"\n  },\n"
                + "  {\n    \"id\": 2,\n    \"title\": \"Title 2\"\n  }\n]");
    }
}