package net.slc.jgroph.Framework.Web.Router;

import java.util.Map;
import java.util.HashMap;

/**
 * Take care of routing Web requests to pre-defined actions.
 */
public class Router
{
	/**
	 * Map action sources, which are couples of request methods and request
	 * paths, to the actual actions that must be performed in response to such
	 * requests.
	 */
	private Map<ActionSource, Action> actions = new HashMap<>();

	/**
	 * Register a new action for a given source.
	 * 
	 * @param source the source that will cause the given action execution.
	 * @param action  the action to be executed in response to the given source.
	 */
	public void register(ActionSource source, Action action)
	{
		actions.put(source, action);
	}

	/**
	 * Route the given request to the appropriate action, and execute it passing
	 * the given request and response.
	 * 
	 * @param request  the current request
	 * @param response the current response
	 * @throws NoMatchingActionFoundException if no action could be found for
	 * the given request.
	 */
	public void route(Request request, Response response)
	{
		Action action = actions.get(new ActionSource(request.getMethod(), request.getPath()));
		if (action == null) {
			throw new NoMatchingActionFoundException(request);
		}
		
		action.execute(request, response);
	}
}