package net.slc.jgroph.Framework.Web.Router;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Arrays;

public class RouterTest
{
	@Test
	public void actionMatchesRequest()
	{
		Action getDashboard = mock(Action.class);
		Action createResource = mock(Action.class);

		Routes routes = createRoutes(
			new Route(Request.Method.GET, "/", getDashboard),
			new Route(Request.Method.POST, "/resource", createResource)
		);
		Router router = new Router(routes);

		Request request = createRequest(Request.Method.POST, "/resource");
		Response response = mock(Response.class);
		router.route(request, response);
		verify(createResource, times(1)).execute(request, response);
		verifyZeroInteractions(getDashboard);

		request = createRequest(Request.Method.GET, "/");
		router.route(request, response);
		verify(getDashboard, times(1)).execute(request, response);
		verifyZeroInteractions(createResource);
	}

	@Test
	public void registerMultipleActionsToTheSamePath()
	{
		Action getResource = mock(Action.class);
		Action deleteResource = mock(Action.class);

		Routes routes = createRoutes(
			new Route(Request.Method.GET, "/resource/123", getResource),
			new Route(Request.Method.DELETE, "/resource/123", deleteResource)
		);
		Router router = new Router(routes);
		
		Request request = createRequest(Request.Method.GET, "/resource/123");
		Response response = mock(Response.class);
		router.route(request, response);
		verify(getResource, times(1)).execute(request, response);
		verifyZeroInteractions(deleteResource);

		request = createRequest(Request.Method.DELETE, "/resource/123");
		router.route(request, response);
		verify(deleteResource, times(1)).execute(request, response);
		verifyZeroInteractions(getResource);
	}

	@Test
	public void canUseRegexInPath()
	{
		Action getResource = mock(Action.class);
		Action editTag = mock(Action.class);

		Routes routes = createRoutes(
			new Route(Request.Method.GET, "/resource/\\d+/?", getResource),
			new Route(Request.Method.PUT, "/tag/\\d+/?", editTag)
		);
		Router router = new Router(routes);
		
		Request request = createRequest(Request.Method.GET, "/resource/123");
		Response response = mock(Response.class);
		router.route(request, response);
		verify(getResource, times(1)).execute(request, response);
		verifyZeroInteractions(editTag);

		request = createRequest(Request.Method.PUT, "/tag/123/");
		router.route(request, response);
		verify(editTag, times(1)).execute(request, response);
		verifyZeroInteractions(getResource);
	}

	@Test
	public void ifMultipleMatchesFirstActionDefinitionIsPicked()
	{
		Action getSpecificResource = mock(Action.class);
		Action getGenericResource = mock(Action.class);

		Routes routes = createRoutes(
			new Route(Request.Method.GET, "/resource/123", getSpecificResource),
			new Route(Request.Method.GET, "/resource/\\d+/?", getGenericResource)
		);
		Router router = new Router(routes);

		Request request = createRequest(Request.Method.GET, "/resource/123");
		Response response = mock(Response.class);
		router.route(request, response);
		verify(getSpecificResource, times(1)).execute(request, response);
		verifyZeroInteractions(getGenericResource);

		request = createRequest(Request.Method.GET, "/resource/456");
		router.route(request, response);
		verify(getGenericResource, times(1)).execute(request, response);
		verifyZeroInteractions(getSpecificResource);
	}

	@Test(expected=NoMatchingActionFoundException.class)
	public void failIfNoActionRequestRegistered()
	{
		Router router = new Router(createRoutes());
		Request request = createRequest(Request.Method.GET, "/");
		router.route(request, mock(Response.class));
	}

	@Test(expected=NoMatchingActionFoundException.class)
	public void failIfNoMatchingActionRegistered()
	{
		Routes routes = createRoutes(new Route(Request.Method.GET, "/", mock(Action.class)));
		Router router = new Router(routes);

		Request request = createRequest(Request.Method.POST, "/");
		router.route(request, mock(Response.class));
	}

	@Test
	public void notMatchingActionExceptionContainsOriginalRequest()
	{
		Router router = new Router(createRoutes());
		Request request = createRequest(Request.Method.GET, "/");
		NoMatchingActionFoundException exception = null;
		try {
			router.route(request, mock(Response.class));
		} catch (NoMatchingActionFoundException e) {
			exception = e;
		}

		assertSame(exception.getRequest(), request);
	}

	private Routes createRoutes(Route... routes)
	{
		Boolean[] hasNext = new Boolean[routes.length];
		for (int i = 0; i < routes.length - 1; i++) {
			hasNext[i] = true;
		}
		if (routes.length > 0) {
			hasNext[routes.length - 1] = false;
		}

		Route[] next = routes.length > 0 ? Arrays.copyOfRange(routes, 1, routes.length) : new Route[0];

		Routes routesMock = mock(Routes.class);
		when(routesMock.iterator()).thenAnswer((invocation) -> {
			Iterator<Route> iterator = mock(Iterator.class);
			when(iterator.hasNext()).thenReturn(routes.length > 0, hasNext);
			when(iterator.next()).thenReturn(routes.length > 0 ? routes[0] : null, next);
			return iterator;
		});
		
		return routesMock;
	}

	private Request createRequest(Request.Method method, String path)
	{
		Request request = mock(Request.class);
		when(request.getMethod()).thenReturn(method);
		when(request.getPath()).thenReturn(path);
		return request;
	}
}