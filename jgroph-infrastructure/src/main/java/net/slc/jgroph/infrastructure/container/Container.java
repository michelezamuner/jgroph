package net.slc.jgroph.infrastructure.container;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Container
{
    private final Map<Class, Object> bound = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T make(final Class<T> type, final Object... args)
    {
        if (type == Container.class) {
            return (T)this;
        }

        if (bound.containsKey(type)) {
            return (T)bound.get(type);
        }

        final Constructor<T> constructor = getConstructor(type);
        if (constructor == null) {
            throw new ContainerError(String.format("Cannot instantiate %s with no object bound.", type));
        }

        try {
            return createInstance(constructor, args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ContainerError(e.getMessage(), e);
        }
    }

    public <T> void bind(final Class<T> type, final T instance)
    {
        if (type == null) {
            throw new NullPointerException("Cannot bind null types.");
        }

        if (instance == null) {
            throw new NullPointerException("Cannot bind null instances.");
        }

        bound.put(type, instance);
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> getConstructor(final Class<T> type)
    {
        final Constructor[] constructors = type.getConstructors();
        if (constructors.length == 0) {
            return null;
        }

        if (constructors.length > 1) {
            throw new ContainerError("Cannot instantiate classes with multiple constructors.");
        }

        return (Constructor<T>)constructors[0];
    }

    @SuppressWarnings("unchecked")
    private <T> T createInstance(final Constructor<T> constructor, final Object... args)
            throws InstantiationException, IllegalAccessException, InvocationTargetException
    {
        final Class[] params = constructor.getParameterTypes();

        if (params.length == 0) {
            return constructor.newInstance();
        }

        if (args.length == 0) {
            final Object[] actual = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                actual[i] = make(params[i]);
            }
            return constructor.newInstance(actual);
        }

        if (params.length != args.length) {
            throw new ContainerError("Cannot instantiate classes with partial explicit arguments.");
        }

        return constructor.newInstance(args);
    }
}