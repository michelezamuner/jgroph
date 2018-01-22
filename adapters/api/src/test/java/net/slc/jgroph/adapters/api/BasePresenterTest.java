package net.slc.jgroph.adapters.api;

import com.github.javafaker.Faker;
import com.google.gson.JsonObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
@SuppressWarnings("initialization")
public class BasePresenterTest
{
    @Parameters
    public static Collection<JsonObject[]> getData()
    {
        final Faker faker = new Faker();
        final String special = "Message 'with' <HTML> char=acters";

        return Stream.of(faker.lorem().sentence(), special)
                .map(BasePresenterTest::toJson)
                .collect(Collectors.toList());
    }

    private static JsonObject[] toJson(final String message)
    {
        final JsonObject output = new JsonObject();
        output.addProperty("message", message);
        return new JsonObject[]{output};
    }

    @Parameter public JsonObject jsonOutput;
    @Rule public final MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule public final TestOutputRule output = new TestOutputRule();
    @Mock private HttpServletResponse response;
    @InjectMocks private BasePresenter presenter;

    @Test
    public void doesNotEscapeCharacters()
            throws IOException
    {
        when(response.getWriter()).thenReturn(output.getWriter());
        presenter.render(jsonOutput);

        final String json = String.format("{\n  \"message\": \"%s\"\n}", jsonOutput.get("message").getAsString());
        output.assertOutputEquals(json);
    }
}