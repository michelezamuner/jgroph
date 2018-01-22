package net.slc.jgroph.adapters.remoteconsole.router;

import com.github.javafaker.Faker;
import net.slc.jgroph.adapters.remoteconsole.ResourceController;
import net.slc.jgroph.adapters.remoteconsole.ConsoleResourcePresenter;
import net.slc.jgroph.infrastructure.container.Container;
import net.slc.jgroph.application.ResourcePresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("initialization")
public class RouterTest
{
    private final Faker faker = new Faker();
    @Mock private ResourceController controller;
    @Mock private Container container;
    @Mock private Response response;
    @Mock private ConsoleResourcePresenter presenter;
    @InjectMocks private Router router;

    @Before
    public void setUp()
    {
        when(container.make(ResourceController.class)).thenReturn(controller);
    }

    @Test
    public void resourceRequestIsRoutedToShowResource()
            throws IOException, BadRequestException, NotFoundException
    {
        final String resourceId = String.valueOf(faker.number().randomNumber());
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
        when(container.make(ConsoleResourcePresenter.class, response)).thenReturn(presenter);
        try {
            router.route(new Request("GET /resources"), response);
        } catch (Exception e) {
            // Test fails if request cannot be routed
        }
        verify(container).bind(ResourcePresenter.class, presenter);
    }
}