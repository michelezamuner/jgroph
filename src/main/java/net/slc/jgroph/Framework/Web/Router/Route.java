package net.slc.jgroph.Framework.Web.Router;

public class Route
{
	private Request.Method method;
	private String path;
	private Action action;

	public Route(Request.Method method, String path, Action action)
	{
		this.method = method;
		this.path = path;
		this.action = action;
	}

	public Request.Method getMethod()
	{
		return method;
	}

	public String getPath()
	{
		return path;
	}

	public Action getAction()
	{
		return action;
	}
}