package net.slc.jgroph.doubles;

public class SimpleDependencies
{
    private final Simple d1;
    private final Simple d2;

    public SimpleDependencies(Simple d1, Simple d2)
    {
        this.d1 = d1;
        this.d2 = d2;
    }

    public Simple getD1()
    {
        return d1;
    }

    public Simple getD2()
    {
        return d2;
    }
}