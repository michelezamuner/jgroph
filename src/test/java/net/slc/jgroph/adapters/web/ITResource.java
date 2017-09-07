package net.slc.jgroph.adapters.web;

import net.sourceforge.jwebunit.api.HttpHeader;
import net.sourceforge.jwebunit.junit.WebTester;
import org.junit.Before;
import org.junit.Test;

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
        // TODO: move status code into an enumeration
        this.tester.assertResponseCode(200);
        // TODO: move content type into an enumeration
        this.tester.assertHeaderEquals("Content-Type", "application/json");
    }
}