package net.slc.jgroph.adapters.remoteconsole.router;

public class Request
{
    private final Method method;
    private final String prefix;
    private final String path;

    public Request(final String request)
            throws InvalidRequestFormatException, UnsupportedMethodException
    {
        final String[] parts = request.split(" ");
        if (parts.length < 2 || parts.length > 3) {
            throw new InvalidRequestFormatException("Invalid request format.");
        }

        final String methodString = parts[0];
        final String pathString = parts[1];

        try {
            method = Method.valueOf(methodString);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedMethodException("Method " + methodString + " is not supported.", e);
        }

        if (!pathString.startsWith("/")) {
            throw new InvalidRequestFormatException("Request path must start with a slash.");
        }

        final int separatorPosition = pathString.indexOf('/', 1);
        if (separatorPosition == -1) {
            prefix = path = pathString;
        } else {
            prefix = pathString.substring(0, separatorPosition);
            path = pathString.substring(separatorPosition, pathString.length());
        }
    }

    public Method getMethod()
    {
        return method;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public String getPath()
    {
        return path;
    }
}