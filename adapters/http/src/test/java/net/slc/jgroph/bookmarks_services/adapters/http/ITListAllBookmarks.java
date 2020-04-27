package net.slc.jgroph.bookmarks_services.adapters.http;

import net.sourceforge.jwebunit.junit.WebTester;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("initialization")
public class ITListAllBookmarks
{
    private WebTester tester;

    @Before
    public void setUp()
    {
        tester = new WebTester();
        tester.setBaseUrl("http://localhost:8080");
    }

    @Test
    public void allBookmarksAreProperlyRetrieved()
    {
        tester.beginAt("/bookmarks");
        tester.assertResponseCode(Response.SC_OK);
        tester.assertHeaderEquals("Content-Type", "application/json");

        final String expected = "[\n  {\n    \"id\": 1,\n    \"title\": \"Title 1\"\n  },\n"
                + "  {\n    \"id\": 2,\n    \"title\": \"Title 2\"\n  }\n]";
        assertEquals(expected, tester.getPageSource());
    }
}