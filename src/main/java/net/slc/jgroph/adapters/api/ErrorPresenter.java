package net.slc.jgroph.adapters.api;

import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorPresenter extends BasePresenter
{
    public ErrorPresenter(final HttpServletResponse response)
    {
        super(response);
    }

    public void fail(final int status, final String message)
            throws IOException
    {
        response.setStatus(status);
        render((JsonObject output) ->
                output.addProperty("error", message)
        );
    }
}