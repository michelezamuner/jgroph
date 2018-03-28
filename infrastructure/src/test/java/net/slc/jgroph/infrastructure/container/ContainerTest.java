package net.slc.jgroph.infrastructure.container;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import static org.mockito.Mockito.mock;

@SuppressWarnings("initialization")
public class ContainerTest
{
    private Container container;
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp()
    {
        container = new Container();
    }

    @Test
    public void instantiatesNoDependencies()
    {
        final SimpleDouble object = container.make(SimpleDouble.class);
        assertNotNull(object);
    }

    @Test
    public void instantiatesClassWithPublicDefaultConstructor()
    {
        final DefaultConstructorPublicDouble object = container.make(DefaultConstructorPublicDouble.class);
        assertNotNull(object);
    }

    @Test
    public void cannotInstantiateClassWithNonPublicDefaultContructor()
    {
        exception.expect(ContainerError.class);
        exception.expectMessage(
                "Cannot instantiate " + DefaultConstructorDouble.class + " with no public constructor."
        );
        container.make(DefaultConstructorDouble.class);
    }

    @Test
    public void cannotInstantiateClassesWithMultipleConstructors()
    {
        exception.expect(ContainerError.class);
        exception.expectMessage(
                "Cannot instantiate "  + MultipleConstructorsDouble.class + " with multiple constructors."
        );
        container.make(MultipleConstructorsDouble.class);
    }

    @Test
    public void cannotInstantiateClassesWithPartialExplicitArgs()
    {
        exception.expect(ContainerError.class);
        exception.expectMessage(
                "Cannot instantiate " + SimpleDependenciesDouble.class + " with partial explicit arguments."
        );
        container.make(SimpleDependenciesDouble.class, new SimpleDouble());
    }

    @Test
    public void instantiatesSimpleDependenciesWithExplicitArgs()
    {
        final SimpleDouble d1 = new SimpleDouble();
        final SimpleDouble d2 = new SimpleDouble();

        final SimpleDependenciesDouble object = container.make(SimpleDependenciesDouble.class, d1, d2);

        assertSame(d1, object.getD1());
        assertSame(d2, object.getD2());
    }

    @Test
    public void instantiatesSimpleDependencies()
    {
        final SimpleDependenciesDouble object = container.make(SimpleDependenciesDouble.class);
        assertEquals("SimpleDouble", object.getD1().getValue());
        assertEquals("SimpleDouble", object.getD2().getValue());
    }

    @Test
    public void instantiatesComplexDependenciesWithExplicitArgs()
    {
        final SimpleDouble d11 = new SimpleDouble();
        final SimpleDouble d12 = new SimpleDouble();
        final SimpleDouble d2 = new SimpleDouble();

        final ComplexDependenciesDouble object = container.make(
                ComplexDependenciesDouble.class,
                new SimpleDependenciesDouble(d11, d12),
                d2
        );

        assertSame(d11, object.getD1().getD1());
        assertSame(d12, object.getD1().getD2());
        assertSame(d2, object.getD2());
    }

    @Test
    public void instantiateComplexDependenciesWithImplicitArgs()
    {
        final ComplexDependenciesDouble object = container.make(ComplexDependenciesDouble.class);
        assertEquals("SimpleDouble", object.getD1().getD1().getValue());
        assertEquals("SimpleDouble", object.getD1().getD2().getValue());
        assertEquals("SimpleDouble", object.getD2().getValue());
    }

    @Test
    public void returnBoundObjectWhenCalledWithClass()
    {
        final SimpleDouble bound = new SimpleDouble();
        container.bind(SimpleDouble.class, bound);

        final SimpleDouble object = container.make(SimpleDouble.class);
        assertSame(bound, object);
    }

    @Test
    public void cannotInstantiateInterfaceIfNoObjectIsBound()
    {
        exception.expect(ContainerError.class);
        exception.expectMessage("Cannot instantiate " + InterfaceDouble.class + " with no object bound.");
        container.make(InterfaceDouble.class);
    }

    @Test
    public void instantiateInterfaceWithBoundObject()
    {
        final InterfaceDouble bound = mock(InterfaceDouble.class);
        container.bind(InterfaceDouble.class, bound);

        final InterfaceDouble object = container.make(InterfaceDouble.class);
        assertSame(bound, object);
    }

    @Test
    public void instantiatingAppWillAlwaysProduceTheSameObject()
    {
        final Container object = container.make(Container.class);
        assertSame(container, object);
    }
}