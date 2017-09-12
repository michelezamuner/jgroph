package net.slc.jgroph.application;

import com.github.javafaker.Faker;
import net.slc.jgroph.domain.ResourceId;
import org.junit.Before;
import org.junit.Test;

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
            throws InvalidResourceIdFormatException, ResourceNotFoundException
    {
        final String webResourceId = String.valueOf(this.faker.number().randomNumber());
        final ResourceId resourceId = new ResourceId(webResourceId);
        final ResourceData resourceData = new ResourceData(resourceId, this.faker.book().title());
        final ResourcePresenter presenter = mock(ResourcePresenter.class);
        final ResourceRepository repository = mock(ResourceRepository.class);
        when(repository.get(resourceId)).thenReturn(resourceData);

        final ShowResource useCase = new ShowResource(presenter, repository);
        useCase.call(webResourceId);

        verify(presenter).show(eq(resourceData));
    }
}