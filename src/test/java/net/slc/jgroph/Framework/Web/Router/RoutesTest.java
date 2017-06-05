package net.slc.jgroph.Framework.Web.Router;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import java.util.Map;

public class RoutesTest
{
	@Test
	public void defaultsToEmptyRoutesList()
	{
		Routes routes = new Routes();

		int i = 0;
		for (Route route : routes) {
			i++;
		}

		assertEquals(0, i);
	}

	@Test
	public void registeredRoutesAreSavedInTheOriginalOrder()
	{
		String[] paths = { "/", "/resources", "/tags" };
		Request.Method[] methods = { Request.Method.GET, Request.Method.POST, Request.Method.PUT };
		Action[] actions = { mock(Action.class), mock(Action.class), mock(Action.class) };

		Routes routes = new Routes();
		for (int i = 0; i < 3; i++) {
			routes.add(methods[i], paths[i], actions[i]);
		}

		int i = 0;
		for (Route route : routes) {
			assertSame(methods[i], route.getMethod());
			assertEquals(paths[i], route.getPath());
			assertSame(actions[i], route.getAction());
			i++;
		}
	}

	@Test(expected=RouteAlreadyDefinedException.class)
	public void cannotRegisterTheSameRouteTwice()
	{
		Routes routes = new Routes();
		routes.add(Request.Method.GET, "/", mock(Action.class));
		routes.add(Request.Method.GET, "/", mock(Action.class));
	}

	@Test
	public void routeAlreadyDefinedExceptionContainsDefinitionData()
	{
		RouteAlreadyDefinedException exception = null;
		Routes routes = new Routes();
		routes.add(Request.Method.GET, "/", mock(Action.class));
		try {
			routes.add(Request.Method.GET, "/", mock(Action.class));
		} catch (RouteAlreadyDefinedException e) {
			exception = e;
		}

		assertSame(Request.Method.GET, exception.getMethod());
		assertEquals("/", exception.getPath());
	}
}