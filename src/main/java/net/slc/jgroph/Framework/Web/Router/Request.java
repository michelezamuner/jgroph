package net.slc.jgroph.Framework.Web.Router;

/**
 * Define a type all requests must adhere to inside this package.
 */
public interface Request
{
	/**
	 * HTTP methods available.
	 */
	public enum Method { GET, POST, DELETE }

	/**
	 * Get the HTTP method of the current request.
	 */
	public Method getMethod();

	/**
	 * Get the path of the current request.
	 */
	public String getPath();
}