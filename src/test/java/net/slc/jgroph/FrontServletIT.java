package net.slc.jgroph;

import org.junit.Before;
import org.junit.Test;
import net.sourceforge.jwebunit.junit.WebTester;

public class FrontServletIT
{
    private WebTester tester;

    @Before
    public void setUp()
    {
        this.tester = new WebTester();
        this.tester.setBaseUrl("http://localhost:8080");
    }

    @Test
    public void test()
    {
        this.tester.beginAt("/");
        this.tester.assertTextPresent("Hello, World!");
    }
}