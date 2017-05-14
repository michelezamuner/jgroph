package net.slc.jgroph.Framework.Web.Router;

/**
 * Thrown when a Web request cannot be matched to any defined action.
 */
public class NoMatchingActionFoundException extends RuntimeException
{
	private Request request;
	
	public NoMatchingActionFoundException(Request request)
	{
		this.request = request;
	}

	/**
	 * Get the request that caused this exception.
	 *
	 * @return Request
	 */
	public Request getRequest()
	{
		return request;
	}
}