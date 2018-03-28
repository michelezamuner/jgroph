package net.slc.jgroph.configuration;

import net.slc.jgroph.adapters.inmemorystorage.InMemoryResourceRepository;
import net.slc.jgroph.application.ResourceRepository;
import net.slc.jgroph.infrastructure.container.Container;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("initialization")
public class ProviderTest
{
    @Mock private Container container;
    @Mock private InMemoryResourceRepository repository;

    @Test
    public void wireProperDependencies()
    {
        when(container.make(eq(InMemoryResourceRepository.class), any())).thenReturn(repository);

        final Provider provider = new Provider();
        provider.bootstrap(container);

        verify(container).bind(ResourceRepository.class, repository);
    }
}