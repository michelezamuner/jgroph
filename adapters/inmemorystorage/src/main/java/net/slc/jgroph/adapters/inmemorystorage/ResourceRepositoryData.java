package net.slc.jgroph.adapters.inmemorystorage;

import net.slc.jgroph.domain.InvalidResourceIdFormatException;
import net.slc.jgroph.application.ResourceData;
import net.slc.jgroph.domain.ResourceId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResourceRepositoryData implements Map<ResourceId, ResourceData>
//public class ResourceRepositoryData extends HashMap<ResourceId, ResourceData>
{
    // Class type: FFF (Other). Class index: 000
    private static final long serialVersionUID = 0xFFF_000L;
    private final Map<ResourceId, ResourceData> map = new HashMap<>();

    public ResourceRepositoryData()
    {
        try {
            map.put(new ResourceId("1"), new ResourceData(new ResourceId("1"), "Title 1"));
            map.put(new ResourceId("2"), new ResourceData(new ResourceId("2"), "Title 2"));
        } catch (InvalidResourceIdFormatException e) {
            // TODO: The whole class is going to be replaced anyway.
            System.out.println(e.getMessage());
        }
    }

    @Override
    public int size()
    {
        return map.size();
    }

    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key)
    {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value)
    {
        return map.containsValue(value);
    }

    @Override
    public ResourceData get(final Object key)
    {
        return map.get(key);
    }

    @Override
    public ResourceData put(final ResourceId key, final ResourceData value)
    {
        return map.put(key, value);
    }

    @Override
    public ResourceData remove(final Object key)
    {
        return map.remove(key);
    }

    @Override
    public void putAll(final Map<? extends ResourceId, ? extends ResourceData> source)
    {
        map.putAll(source);
    }

    @Override
    public void clear()
    {
        map.clear();
    }

    @Override
    public Set<ResourceId> keySet()
    {
        return map.keySet();
    }

    @Override
    public Collection<ResourceData> values() {
        return map.values();
    }

    @Override
    public Set<Map.Entry<ResourceId, ResourceData>> entrySet()
    {
        return map.entrySet();
    }

    @Override
    public boolean equals(final Object object)
    {
        return map.equals(object);
    }

    @Override
    public int hashCode()
    {
        return map.hashCode();
    }
}