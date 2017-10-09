package net.slc.jgroph.application;

import com.github.javafaker.Faker;
import net.slc.jgroph.domain.ResourceId;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShowResourceTest
{
    private Faker faker;

    @Before
    public void setUp()
    {
        this.faker = new Faker();
    }

    @Test
    public void presenterIsCalledWithProperData()
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException
    {
        final String webResourceId = String.valueOf(this.faker.number().randomNumber());
        final ResourceId resourceId = new ResourceId(webResourceId);
        final ResourceData resourceData = new ResourceData(resourceId, this.faker.book().title());
        final ResourcePresenter presenter = mock(ResourcePresenter.class);
        final ResourceRepository repository = mock(ResourceRepository.class);
        when(repository.get(resourceId)).thenReturn(resourceData);

        final ShowResource useCase = new ShowResource(presenter, repository);
        useCase.perform(webResourceId);

        verify(presenter).show(eq(resourceData));
    }

    @Test(expected = InvalidResourceIdFormatException.class)
    public void errorIsThrownIfInvalidIdFormatIsUsed()
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException
    {
        final String webResourceId = "invalid-id";
        final ResourcePresenter presenter = mock(ResourcePresenter.class);
        final ResourceRepository repository = mock(ResourceRepository.class);

        final ShowResource useCase = new ShowResource(presenter, repository);
        useCase.perform(webResourceId);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void errorIsThrownIfInvalidResourceIsRequested()
            throws InvalidResourceIdFormatException, ResourceNotFoundException, IOException
    {
        final String webResourceId = String.valueOf(this.faker.number().randomNumber());
        final ResourceId resourceId = new ResourceId(webResourceId);
        final ResourcePresenter presenter = mock(ResourcePresenter.class);

        final ResourceRepository repository = mock(ResourceRepository.class);
        when(repository.get(eq(resourceId))).thenThrow(new ResourceNotFoundException(""));

        final ShowResource useCase = new ShowResource(presenter, repository);
        useCase.perform(webResourceId);
    }
}