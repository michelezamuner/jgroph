package net.slc.jgroph.adapters.remoteconsole.router;

import com.github.javafaker.Faker;
import net.slc.jgroph.adapters.remoteconsole.ErrorPresenter;
import net.slc.jgroph.adapters.remoteconsole.ResourceController;
import net.slc.jgroph.infrastructure.container.Container;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.Parameter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
@SuppressWarnings("initialization")
public class RouterExceptionsTest
{
    @Parameters
    public static Collection<Class[]> getData()
    {
        return Arrays.asList(new Class[][] {
                { BadRequestException.class },
                { NotFoundException.class }
        });
    }

    private final Faker faker = new Faker();
    @Parameter public Class exceptionClass;
    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock private Response response;
    @Mock private ErrorPresenter presenter;
    @Mock private ResourceController controller;
    @Mock private Container container;
    @InjectMocks private Router router;

    @Before
    public void setUp()
    {
        when(container.make(ResourceController.class)).thenReturn(controller);
        when(container.make(ErrorPresenter.class, response)).thenReturn(presenter);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testErrorPresenterIsUsedOnException()
    {
        final String error = faker.lorem().sentence();
        try {
            final Exception exception = (Exception)exceptionClass.getConstructor(String.class).newInstance(error);
            doThrow(exception).when(controller).show(anyString());

            router.route(new Request("GET /resources"), response);
        } catch (Exception e) {
            // Test fails if request cannot be routed
        }

        verify(presenter).show(error);
    }
}