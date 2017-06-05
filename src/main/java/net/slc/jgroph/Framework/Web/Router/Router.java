package net.slc.jgroph.Framework.Web.Router;

public class Router
{
	private Routes routes;

	public Router(Routes routes)
	{
		this.routes = routes;
	}

	public void route(Request request, Response response)
	{
		for (Route route : routes) {
			if (!request.getPath().matches(route.getPath())) {
				continue;
			}

			if (request.getMethod() != route.getMethod()) {
				continue;
			}

			route.getAction().execute(request, response);
			return;
		}

		throw new NoMatchingActionFoundException(request);
	}
}