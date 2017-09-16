package net.slc.jgroph.adapters;

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

public class AppTest
{
    private App app;

    @Before
    public void setUp()
    {
        app = new App();
    }

    @Test
    public void instantiatesNoDependencies()
            throws AppException
    {
        final Simple object = app.make(Simple.class);
        assertNotNull(object);
    }

    @Test(expected = AppException.class)
    public void cannotInstantiateClassesWithMultipleConstructors()
            throws AppException
    {
        app.make(MultipleConstructors.class);
    }

    @Test(expected = AppException.class)
    public void cannotInstantiateClassesWithPartialExplicitArgs()
            throws AppException
    {
        app.make(SimpleDependencies.class, new Simple());
    }

    @Test
    public void instantiatesSimpleDependenciesWithExplicitArgs()
            throws AppException
    {
        final Simple d1 = new Simple();
        final Simple d2 = new Simple();
        final SimpleDependencies object = app.make(SimpleDependencies.class, d1, d2);
        assertSame(d1, object.getD1());
        assertSame(d2, object.getD2());
    }

    @Test
    public void instantiatesSimpleDependencies()
            throws AppException
    {
        final SimpleDependencies object = app.make(SimpleDependencies.class);
        assertEquals("Simple", object.getD1().getValue());
        assertEquals("Simple", object.getD2().getValue());
    }

    @Test
    public void instantiatesComplexDependenciesWithExplicitArgs()
            throws AppException
    {
        final Simple d11 = new Simple();
        final Simple d12 = new Simple();
        final Simple d2 = new Simple();
        final ComplexDependencies object = app.make(ComplexDependencies.class, new SimpleDependencies(d11, d12), d2);
        assertSame(d11, object.getD1().getD1());
        assertSame(d12, object.getD1().getD2());
        assertSame(d2, object.getD2());
    }

    @Test
    public void instantiateComplexDependenciesWithImplicitArgs()
            throws AppException
    {
        final ComplexDependencies object = app.make(ComplexDependencies.class);
        assertEquals("Simple", object.getD1().getD1().getValue());
        assertEquals("Simple", object.getD1().getD2().getValue());
        assertEquals("Simple", object.getD2().getValue());
    }

    @Test
    public void returnBoundObjectWhenCalledWithClass()
            throws AppException
    {
        final Simple bound = new Simple();
        app.bind(Simple.class, bound);
        final Simple object = app.make(Simple.class);
        assertSame(bound, object);
    }

    @Test(expected = AppException.class)
    public void cannotInstantiateInterfaceIfNoObjectIsBound()
            throws AppException
    {
        app.make(Interface.class);
    }

    @Test
    public void instantiateInterfaceWithBoundObject()
            throws AppException
    {
        Interface bound = mock(Interface.class);
        app.bind(Interface.class, bound);
        Interface object = app.make(Interface.class);
        assertSame(bound, object);
    }

    @Test
    public void instantiatingAppWillAlwaysProduceTheSameObject()
            throws AppException
    {
        App object = app.make(App.class);
        assertSame(app, object);
    }
}