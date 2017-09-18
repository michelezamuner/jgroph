package net.slc.jgroph.infrastructure.container;

import net.slc.jgroph.doubles.ComplexDependencies;
import net.slc.jgroph.doubles.MultipleConstructors;
import net.slc.jgroph.doubles.Simple;
import net.slc.jgroph.doubles.SimpleDependencies;
import net.slc.jgroph.doubles.Interface;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import static org.mockito.Mockito.mock;

public class ContainerTest
{
    private Container container;

    @Before
    public void setUp()
    {
        container = new Container();
    }

    @Test
    public void instantiatesNoDependencies()
            throws ContainerException
    {
        final Simple object = container.make(Simple.class);
        assertNotNull(object);
    }

    @Test(expected = ContainerException.class)
    public void cannotInstantiateClassesWithMultipleConstructors()
            throws ContainerException
    {
        container.make(MultipleConstructors.class);
    }

    @Test(expected = ContainerException.class)
    public void cannotInstantiateClassesWithPartialExplicitArgs()
            throws ContainerException
    {
        container.make(SimpleDependencies.class, new Simple());
    }

    @Test
    public void instantiatesSimpleDependenciesWithExplicitArgs()
            throws ContainerException
    {
        final Simple d1 = new Simple();
        final Simple d2 = new Simple();
        final SimpleDependencies object = container.make(SimpleDependencies.class, d1, d2);
        assertSame(d1, object.getD1());
        assertSame(d2, object.getD2());
    }

    @Test
    public void instantiatesSimpleDependencies()
            throws ContainerException
    {
        final SimpleDependencies object = container.make(SimpleDependencies.class);
        assertEquals("Simple", object.getD1().getValue());
        assertEquals("Simple", object.getD2().getValue());
    }

    @Test
    public void instantiatesComplexDependenciesWithExplicitArgs()
            throws ContainerException
    {
        final Simple d11 = new Simple();
        final Simple d12 = new Simple();
        final Simple d2 = new Simple();
        final ComplexDependencies object = container.make(ComplexDependencies.class, new SimpleDependencies(d11, d12), d2);
        assertSame(d11, object.getD1().getD1());
        assertSame(d12, object.getD1().getD2());
        assertSame(d2, object.getD2());
    }

    @Test
    public void instantiateComplexDependenciesWithImplicitArgs()
            throws ContainerException
    {
        final ComplexDependencies object = container.make(ComplexDependencies.class);
        assertEquals("Simple", object.getD1().getD1().getValue());
        assertEquals("Simple", object.getD1().getD2().getValue());
        assertEquals("Simple", object.getD2().getValue());
    }

    @Test
    public void returnBoundObjectWhenCalledWithClass()
            throws ContainerException
    {
        final Simple bound = new Simple();
        container.bind(Simple.class, bound);
        final Simple object = container.make(Simple.class);
        assertSame(bound, object);
    }

    @Test(expected = ContainerException.class)
    public void cannotInstantiateInterfaceIfNoObjectIsBound()
            throws ContainerException
    {
        container.make(Interface.class);
    }

    @Test
    public void instantiateInterfaceWithBoundObject()
            throws ContainerException
    {
        Interface bound = mock(Interface.class);
        container.bind(Interface.class, bound);
        Interface object = container.make(Interface.class);
        assertSame(bound, object);
    }

    @Test
    public void instantiatingAppWillAlwaysProduceTheSameObject()
            throws ContainerException
    {
        Container object = container.make(Container.class);
        assertSame(container, object);
    }
}