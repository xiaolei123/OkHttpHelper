package com.xiaolei.okhttputil.Stream;

import java.io.IOException;
import java.io.InputStream;

/**
 * 代理的一个输入流，为了是在往外写的时候，保存至本地，待实现
 * Created by xiaolei on 2018/3/1.
 */

public class ProxyInputStream extends InputStream
{
    private InputStream inputStream;
    
    public ProxyInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        return inputStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        return inputStream.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException
    {
        return inputStream.skip(n);
    }

    @Override
    public int available() throws IOException
    {
        return inputStream.available();
    }

    @Override
    public void close() throws IOException
    {
        inputStream.close();
    }

    @Override
    public synchronized void mark(int readlimit)
    {
        inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException
    {
        inputStream.reset();
    }

    @Override
    public boolean markSupported()
    {
        return inputStream.markSupported();
    }

    @Override
    public int read() throws IOException
    {
        return inputStream.read();
    }
}
