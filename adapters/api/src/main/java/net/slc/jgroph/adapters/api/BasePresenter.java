package net.slc.jgroph.adapters.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BasePresenter
{
    protected final HttpServletResponse response;

    BasePresenter(final HttpServletResponse response)
    {
        this.response = response;
    }

    public void render(final JsonObject output)
            throws IOException
    {
        response.setHeader("Content-Type", "application/json");

        final Gson json = new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();
        response.getWriter().print(json.toJson(output));
    }
}