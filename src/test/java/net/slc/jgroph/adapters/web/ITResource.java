package net.slc.jgroph.adapters.web;

import net.sourceforge.jwebunit.api.HttpHeader;
import net.sourceforge.jwebunit.junit.WebTester;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.List;

public class ITResource
{
    private WebTester tester;

    @Before
    public void setUp()
    {
        this.tester = new WebTester();
        // TODO: get URL and port from the environment
        this.tester.setBaseUrl("http://localhost:8080");
    }

    @Test
    public void theRequestedResourceIsCorrectlyRetrieved()
    {
        this.tester.beginAt("/resource/1");
        List<HttpHeader> headers = this.tester.getResponseHeaders();
        System.out.println("########## HEADERS #########");
        for (HttpHeader header : headers) {
            System.out.println(header.getName() + ": " + header.getValue());
        }
        System.out.println("###################");
        // TODO: use enumeration for status codes
        this.tester.assertResponseCode(200);
        // TODO: use enumeration for content types
        this.tester.assertHeaderEquals("Content-Type", "application/json");
        assertEquals("{\n\t\"id\": 1,\n\t\"title\": \"Title 1\"\n}", this.tester.getPageSource());
    }
}