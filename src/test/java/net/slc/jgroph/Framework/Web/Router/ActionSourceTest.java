package net.slc.jgroph.Framework.Web.Router;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class ActionSourceTest
{
	@Test
	public void properlyImplementsEquals()
	{
		ActionSource source = new ActionSource(Request.Method.GET, "/");
		assertTrue(source.equals(source));
		assertTrue(source.equals(new ActionSource(Request.Method.GET, "/")));
		assertFalse(source.equals(null));
		assertFalse(source.equals(new Integer(0)));
		assertFalse(source.equals(new ActionSource(Request.Method.GET, "/resource")));
		assertFalse(source.equals(new ActionSource(Request.Method.POST, "/")));
	}

	@Test
	public void properlyImplementsHashCode()
	{
		ActionSource source = new ActionSource(Request.Method.GET, "/");
		assertTrue(source.hashCode() == new ActionSource(Request.Method.GET, "/").hashCode());
		assertFalse(source.hashCode() == new ActionSource(Request.Method.GET, "/resource").hashCode());
		assertFalse(source.hashCode() == new ActionSource(Request.Method.POST, "/").hashCode());
	}
}