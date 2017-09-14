package net.slc.jgroph.adapters.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.slc.jgroph.application.ResourceData;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResourcePresenter implements net.slc.jgroph.application.ResourcePresenter
{
    private final HttpServletResponse response;

    public ResourcePresenter(final HttpServletResponse response)
    {
        this.response = response;
    }

    @Override
    public void show(final ResourceData data)
            throws IOException
    {
        this.response.setStatus(200);
        this.response.setHeader("Content-Type", "application/json");

        final Gson json = new GsonBuilder().setPrettyPrinting().create();
        final JsonObject output = new JsonObject();
        output.addProperty("id", data.getId().toString());
        output.addProperty("title", data.getTitle());

        this.response.getWriter().print(json.toJson(output));
    }
}