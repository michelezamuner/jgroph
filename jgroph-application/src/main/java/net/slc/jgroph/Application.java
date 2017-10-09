package net.slc.jgroph;

import net.slc.jgroph.adapters.inmemorystorage.InMemoryResourceRepository;
import net.slc.jgroph.adapters.inmemorystorage.ResourceRepositoryData;
import net.slc.jgroph.infrastructure.container.Container;

public class Application
{
    public void bootstrap(final Container container)
    {
        final ResourceRepositoryData data = container.make(ResourceRepositoryData.class);
        final InMemoryResourceRepository repository = container.make(InMemoryResourceRepository.class, data);
        container.bind(net.slc.jgroph.application.ResourceRepository.class, repository);
    }
}