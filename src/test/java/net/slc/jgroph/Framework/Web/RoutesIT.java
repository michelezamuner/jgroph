package net.slc.jgroph.Framework.Web;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.IOException;

public class RoutesIT
{
	@Test
	public void canReachDashboard()
		throws MalformedURLException, IOException
	{
		URL url = new URL("http://localhost:8080/");
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		assertEquals(200, connection.getResponseCode());
	}
}