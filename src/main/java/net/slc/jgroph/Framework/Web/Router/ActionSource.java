package net.slc.jgroph.Framework.Web.Router;

/**
 * Represent the source of an action, which is the couple of request method and
 * request path used to know when an action needs to be performed.
 */
class ActionSource
{
	private Request.Method method;
	private String path;

	/**
	 * Cache the hash code in case of multiple usages.
	 */
	private int hashCode;

	public ActionSource(Request.Method method, String path)
	{
		this.method = method;
		this.path = path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object object)
	{
		if (this == object) {
			return true;
		}

		if (object == null) {
			return false;
		}

		if (object instanceof ActionSource == false) {
			return false;
		}

		ActionSource request = (ActionSource)object;
		return method.equals(request.method) && path.equals(request.path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		if (hashCode == 0) {
			hashCode = ("" + method + path).hashCode();
		}

		return hashCode;
	}
}