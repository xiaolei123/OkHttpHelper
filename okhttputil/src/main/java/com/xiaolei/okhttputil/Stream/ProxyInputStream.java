package com.xiaolei.okhttputil.Stream;

import com.xiaolei.okhttputil.Catch.Interfaces.CacheInterface;
import com.xiaolei.okhttputil.Utils.Util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * 代理的一个输入流，为了是在往外写的时候，保存至本地，待实现
 * Created by xiaolei on 2018/3/1.
 */

public class ProxyInputStream extends InputStream
{
    private InputStream inputStream;
    private String key;
    private CacheInterface cacheImpl;

    public ProxyInputStream(InputStream inputStream, String key, CacheInterface cacheImpl)
    {
        this.inputStream = inputStream;
        this.key = key;
        this.cacheImpl = cacheImpl;
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        return super.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        return super.read(b, off, len);
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
        // 我选择在网络数据流往外写的时候，顺便保存一份保存到数据库
        // 即保存了一份到本地，又不影响性能，特别是在大文件缓存处理的时候
        int b = inputStream.read();
        if (b != -1)
        {
            byteBuffer.put((byte) b);
        }
        if ((b == -1 && byteBuffer.position() > 0) || byteBuffer.position() == (maxSize - 1)) // 到流的结尾，或者缓冲区已满
        {
            // 进行保存操作
            byteBuffer.flip();// 转换角色，从写的状态，回到读取状态
            byte buff[] = getBytesFromBuffer(byteBuffer);
            if (isFirst)
            {
                // 第一次保存，则覆盖之前
                cacheImpl.put(key, new ByteArrayInputStream(buff));
                isFirst = false;
            } else
            {
                // 第二次保存，则往后追加
                cacheImpl.append(key, new ByteArrayInputStream(buff));
            }
        }
        return b;
    }

    private int maxSize = 1024 * 10;//初始化缓冲区设置为4K
    private ByteBuffer byteBuffer = ByteBuffer.allocate(maxSize);
    private boolean isFirst = true;

    /**
     * 从缓冲区中取得byte数组
     *
     * @param byteBuffer
     * @return
     */
    private byte[] getBytesFromBuffer(ByteBuffer byteBuffer)
    {
        byte buff[] = new byte[byteBuffer.remaining()];
        byteBuffer.get(buff, 0, buff.length);
        byteBuffer.clear();
        return buff;
    }
}
