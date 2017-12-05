package net.slc.jgroph.adapters.api;

import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class PresenterDouble extends BasePresenter
{
    PresenterDouble(final HttpServletResponse response)
    {
        super(response);
    }

    void render(String message)
            throws IOException
    {
        render((JsonObject output) ->
                output.addProperty("message", message)
        );
    }
}