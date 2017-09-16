package net.slc.jgroph.doubles;

public class ComplexDependencies
{
    private final SimpleDependencies d1;
    private final Simple d2;

    public ComplexDependencies(SimpleDependencies d1, Simple d2)
    {
        this.d1 = d1;
        this.d2 = d2;
    }

    public SimpleDependencies getD1()
    {
        return d1;
    }

    public Simple getD2()
    {
        return d2;
    }
}