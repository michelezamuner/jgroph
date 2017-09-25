package net.slc.jgroph.infrastructure.container;

class SimpleDependenciesDouble
{
    private final SimpleDouble d1;
    private final SimpleDouble d2;

    public SimpleDependenciesDouble(SimpleDouble d1, SimpleDouble d2)
    {
        this.d1 = d1;
        this.d2 = d2;
    }

    SimpleDouble getD1()
    {
        return d1;
    }

    SimpleDouble getD2()
    {
        return d2;
    }
}