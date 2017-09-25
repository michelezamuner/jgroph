package net.slc.jgroph.adapters.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Consumer;

public class BasePresenter
{
    protected final HttpServletResponse response;

    public BasePresenter(final HttpServletResponse response)
    {
        this.response = response;
    }

    public void render(final Consumer<JsonObject> callback)
            throws IOException
    {
        response.setHeader("Content-Type", "application/json");

        final Gson json = new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();
        final JsonObject output = new JsonObject();

        callback.accept(output);

        response.getWriter().print(json.toJson(output));
    }
}