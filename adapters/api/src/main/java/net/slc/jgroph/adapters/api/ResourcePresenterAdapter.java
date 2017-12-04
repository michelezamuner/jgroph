package net.slc.jgroph.adapters.api;

import com.google.gson.JsonObject;
import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.application.ResourcePresenter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResourcePresenterAdapter extends BasePresenter implements ResourcePresenter
{
    public ResourcePresenterAdapter(final HttpServletResponse response)
    {
        super(response);
    }

    @Override
    public void show(final ResourceData data)
            throws IOException
    {
        response.setStatus(200);
        render((JsonObject output) -> {
            output.addProperty("id", data.getId().toInt());
            output.addProperty("title", data.getTitle());
        });
    }
}