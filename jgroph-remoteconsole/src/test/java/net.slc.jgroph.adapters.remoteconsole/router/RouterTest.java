package net.slc.jgroph.adapters.remoteconsole.router;

import com.github.javafaker.Faker;
import net.slc.jgroph.adapters.remoteconsole.ErrorPresenter;
import net.slc.jgroph.adapters.remoteconsole.ResourceController;
import net.slc.jgroph.adapters.remoteconsole.ResourcePresenter;
import net.slc.jgroph.adapters.remoteconsole.router.*;
import net.slc.jgroph.infrastructure.container.Container;

import org.junit.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RouterTest
{
    private final Faker faker = new Faker();

    @Test
    public void resourceRequestIsRoutedToShowResource()
            throws IOException, BadRequestException, NotFoundException
    {
        final String resourceId = String.valueOf(faker.number().randomNumber());
        final ResourceController controller = mock(ResourceController.class);

        final Container container = mock(Container.class);
        when(container.make(ResourceController.class)).thenReturn(controller);

        final Router router = new Router(container);
        try {
            router.route(new Request("GET /resources/" + resourceId), mock(Response.class));
        } catch (Exception e) {
            // Test fails if request cannot be routed
        }

        verify(controller).show(resourceId);
    }

    @Test
    public void resourcePresenterIsBuiltFromTheRealResponse()
    {
        final Response response = mock(Response.class);
        final ResourcePresenter presenter = mock(ResourcePresenter.class);

        final Container container = mock(Container.class);
        when(container.make(ResourcePresenter.class, response)).thenReturn(presenter);
        when(container.make(ResourceController.class)).thenReturn(mock(ResourceController.class));

        final Router router = new Router(container);
        try {
            router.route(new Request("GET /resources"), response);
        } catch (Exception e) {
            // Test fails if request cannot be routed
        }

        verify(container).bind(net.slc.jgroph.application.ResourcePresenter.class, presenter);
    }

    @Test
    public void errorPresenterIsUsedOnException()
    {
        testErrorPresenterIsUsedOnException(BadRequestException.class);
        testErrorPresenterIsUsedOnException(NotFoundException.class);
    }

    @SuppressWarnings("unchecked")
    private void testErrorPresenterIsUsedOnException(final Class exceptionClass)
    {
        final String error = faker.lorem().sentence();
        final Response response = mock(Response.class);
        final ErrorPresenter presenter = mock(ErrorPresenter.class);

        final ResourceController controller = mock(ResourceController.class);
        try {
            final Exception exception = (Exception)exceptionClass.getConstructor(String.class).newInstance(error);
            doThrow(exception).when(controller).show(anyString());
        } catch (Exception e) {
            // Test fails if the exception cannot be thrown
        }

        final Container container = mock(Container.class);
        when(container.make(ResourceController.class)).thenReturn(controller);
        when(container.make(ErrorPresenter.class, response)).thenReturn(presenter);

        final Router router = new Router(container);
        try {
            router.route(new Request("GET /resources"), response);
        } catch (Exception e) {
            // Test fails if request cannot be routed
        }

        verify(presenter).show(error);
    }
}