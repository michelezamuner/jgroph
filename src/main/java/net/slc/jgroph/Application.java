package net.slc.jgroph;

import net.slc.jgroph.adapters.inmemorystorage.ResourceRepository;
import net.slc.jgroph.adapters.inmemorystorage.ResourceRepositoryData;
import net.slc.jgroph.infrastructure.container.Container;

public class Application
{
    public void bootstrap(final Container container)
    {
        final ResourceRepositoryData data = container.make(ResourceRepositoryData.class);
        final ResourceRepository repository = container.make(ResourceRepository.class, data);
        container.bind(net.slc.jgroph.application.ResourceRepository.class, repository);
    }
}