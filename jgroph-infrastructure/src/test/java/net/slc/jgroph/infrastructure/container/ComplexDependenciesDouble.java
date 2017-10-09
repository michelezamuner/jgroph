package net.slc.jgroph.infrastructure.container;

class ComplexDependenciesDouble
{
    private final SimpleDependenciesDouble d1;
    private final SimpleDouble d2;

    public ComplexDependenciesDouble(final SimpleDependenciesDouble d1, final SimpleDouble d2)
    {
        this.d1 = d1;
        this.d2 = d2;
    }

    SimpleDependenciesDouble getD1()
    {
        return d1;
    }

    SimpleDouble getD2()
    {
        return d2;
    }
}