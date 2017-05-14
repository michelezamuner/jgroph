package net.slc.jgroph.Framework.Web.Router;

import org.junit.Test;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class RouterTest
{
	@Test
	public void actionMatchesRequest()
	{
		Action dashboardAction = mock(Action.class);
		Action createResourceAction = mock(Action.class);

		Router router = new Router();
		router.register(new ActionSource(Request.Method.GET, "/"), dashboardAction);
		router.register(new ActionSource(Request.Method.POST, "/resource"), createResourceAction);

		Request request = createRequest(Request.Method.POST, "/resource");
		Response response = mock(Response.class);
		router.route(request, response);
		verify(createResourceAction, times(1)).execute(request, response);

		request = createRequest(Request.Method.GET, "/");
		router.route(request, response);
		verify(dashboardAction, times(1)).execute(request, response);
	}

	@Test
	public void registerMultipleActionsToTheSamePath()
	{
		Action getResource = mock(Action.class);
		Action deleteResource = mock(Action.class);

		Router router = new Router();
		router.register(new ActionSource(Request.Method.GET, "/resource/123"), getResource);
		router.register(new ActionSource(Request.Method.DELETE, "/resource/123"), deleteResource);

		Request request = createRequest(Request.Method.GET, "/resource/123");
		Response response = mock(Response.class);
		router.route(request, response);
		verify(getResource, times(1)).execute(request, response);

		request = createRequest(Request.Method.DELETE, "/resource/123");
		router.route(request, response);
		verify(deleteResource, times(1)).execute(request, response);
	}

	@Test(expected=NoMatchingActionFoundException.class)
	public void failIfNoActionRequestRegistered()
	{
		Router router = new Router();
		Request request = createRequest(Request.Method.GET, "/");
		router.route(request, mock(Response.class));
	}

	@Test(expected=NoMatchingActionFoundException.class)
	public void failIfNoMatchingActionRegistered()
	{
		Action action = mock(Action.class);
		Router router = new Router();
		router.register(new ActionSource(Request.Method.GET, "/"), action);

		Request request = createRequest(Request.Method.POST, "/");
		router.route(request, mock(Response.class));
	}

	@Test
	public void notMatchingActionExceptionContainsOriginalRequest()
	{
		Router router = new Router();
		Request request = createRequest(Request.Method.GET, "/");
		NoMatchingActionFoundException exception = null;
		try {
			router.route(request, mock(Response.class));
		} catch (NoMatchingActionFoundException e) {
			exception = e;
		}

		assertSame(exception.getRequest(), request);
	}

	private Request createRequest(Request.Method method, String path)
	{
		Request request = mock(Request.class);
		when(request.getMethod()).thenReturn(method);
		when(request.getPath()).thenReturn(path);
		return request;
	}
}