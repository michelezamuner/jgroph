package net.slc.jgroph.bookmarks_services.adapters.http;

public class BookmarksController
{
    void index(final Request request, final Response response)
            throws ResponseException
    {
        response.setJsonContentType();
        response.write("[\n  {\n    \"id\": 1,\n    \"title\": \"Title 1\"\n  },\n"
                + "  {\n    \"id\": 2,\n    \"title\": \"Title 2\"\n  }\n]");
    }
}