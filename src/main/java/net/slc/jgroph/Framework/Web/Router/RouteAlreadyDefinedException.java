package net.slc.jgroph.Framework.Web.Router;

public class RouteAlreadyDefinedException extends RuntimeException
{
	private Request.Method method;
	private String path;

	public RouteAlreadyDefinedException(Request.Method method, String path)
	{
		this.method = method;
		this.path = path;
	}

	public Request.Method getMethod()
	{
		return method;
	}

	public String getPath()
	{
		return path;
	}
}