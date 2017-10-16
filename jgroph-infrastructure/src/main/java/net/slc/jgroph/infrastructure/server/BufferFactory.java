package net.slc.jgroph.infrastructure.server;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

public class BufferFactory
{
    public static final int DEFAULT_SIZE = 2048;
    public static final Charset DEFAULT_CHARSET = UTF_8;

    public ByteBuffer createForRead(final int size)
    {
        return ByteBuffer.allocate(size);
    }

    public ByteBuffer createForRead()
    {
        return createForRead(DEFAULT_SIZE);
    }

    public ByteBuffer createForWrite(final String string)
    {
        return ByteBuffer.wrap(string.getBytes(DEFAULT_CHARSET));
    }
}