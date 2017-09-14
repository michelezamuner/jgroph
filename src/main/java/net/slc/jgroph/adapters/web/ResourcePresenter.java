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
        final Gson json = new GsonBuilder().setPrettyPrinting().create();
        final JsonObject result = new JsonObject();
        result.addProperty("id", data.getId().toString());
        result.addProperty("title", data.getTitle());
        System.out.println(json.toJson(result));
        this.response.getWriter().print(json.toJson(result));
    }
}