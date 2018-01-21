package net.slc.jgroph.adapters.api;

import org.junit.ComparisonFailure;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

class TestOutputRule implements TestRule
{
    private final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    private final PrintWriter writer = new PrintWriter(new OutputStreamWriter(stream));

    public PrintWriter getWriter() {
        return writer;
    }

    public void assertOutputEquals(final String expected)
            throws UnsupportedEncodingException
    {
        assertOutputEquals(null, expected);
    }

    public void assertOutputEquals(final String message, final String expected)
            throws UnsupportedEncodingException
    {
        writer.flush();

        final String actual = new String(stream.toByteArray(), "UTF-8");

        if (!expected.equals(actual)) {
            final String cleanMessage = message == null ? "" : message;
            throw new ComparisonFailure(cleanMessage, expected, actual);
        }
    }

    @Override
    public Statement apply(Statement statement, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                statement.evaluate();
            }
        };
    }
}