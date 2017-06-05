package net.slc.jgroph.Framework.Web.Router;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Routes implements Iterable<Route>
{
	private List<Route> routes = new ArrayList<>();

	public Iterator<Route> iterator()
	{
		return routes.iterator();
	}

	public void add(Request.Method method, String path, Action action)
	{
		for (Route route : routes) {
			if (route.getMethod() == method && route.getPath().equals(path)) {
				throw new RouteAlreadyDefinedException(method, path);
			}
		}
		
		routes.add(new Route(method, path, action));
	}
}