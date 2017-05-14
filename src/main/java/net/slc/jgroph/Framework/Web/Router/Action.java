package net.slc.jgroph.Framework.Web.Router;

/**
 * Functional interface representing an action to be performed upon a client Web
 * request.
 *
 * @see Router
 */
public interface Action
{
	/**
	 * Execute the action represented by the current object, given the Web
	 * request and response.
	 * 
	 * @param request the client Web request
	 * @param response the client Web response
	 */
	public void execute(Request request, Response response);
}