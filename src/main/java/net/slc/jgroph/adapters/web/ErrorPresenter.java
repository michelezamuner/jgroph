package net.slc.jgroph.adapters.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorPresenter
{
    private final HttpServletResponse response;

    public ErrorPresenter(final HttpServletResponse response)
    {
        this.response = response;
    }

    public void fail(final int status, final String message)
            throws IOException
    {
        this.response.setStatus(status);
        this.response.setHeader("Content-Type", "application/json");

        final Gson json = new GsonBuilder().setPrettyPrinting().create();
        final JsonObject output = new JsonObject();
        output.addProperty("error", status);
        output.addProperty("message", message);

        this.response.getWriter().print(json.toJson(output));
    }
}