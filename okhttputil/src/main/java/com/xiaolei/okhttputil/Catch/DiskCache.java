package com.xiaolei.okhttputil.Catch;


import java.io.*;
import java.util.*;

/**
 * 自己定义的本地文件缓存类
 */
public class DiskCache
{
    private static DiskCache diskCache;
    private File dir = null;

    private DiskCache(File dir, String version)
    {
        if (!dir.exists())
        {
            boolean result = dir.mkdirs();
        }
        this.dir = dir;
    }

    /**
     * 删除原先key，作为新key存数据
     *
     * @param key
     * @param string 需要保存的字符串
     */
    public synchronized void put(String key, String string)
    {
        put(key, new ByteArrayInputStream(string.getBytes()));
    }

    /**
     * 删除原先key，作为新key存数据
     *
     * @param key
     * @param in
     */
    public synchronized void put(final String key, InputStream in)
    {
        File files[] = dir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.isFile() && pathname.getName().split("\\.")[0].equals(key);
            }
        });

        for (File file : files)
        {
            boolean deleteResult = file.delete();
        }
        File file = new File(dir, key + "." + 0);
        createFileByStream(file, in);
    }

    /**
     * 获取原先key，有就从index后+1，没有就index=0
     *
     * @param key
     * @param in
     */
    public synchronized void append(final String key, InputStream in)
    {
        int maxIndex = -1;
        File files[] = dir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.isFile() && pathname.getName().split("\\.")[0].equals(key);
            }
        });

        for (File file : files)
        {
            String indexStr = file.getName().split("\\.")[1];
            int index = -1;
            try
            {
                index = Integer.parseInt(indexStr);
            } catch (Exception e)
            {
                index = -1;
            }
            maxIndex = (index >= maxIndex) ? index : maxIndex;
        }
        File file = new File(dir, key + "." + (++maxIndex));
        createFileByStream(file, in);
    }

    /**
     * 获取原先key，有就从index后+1，没有就index=0
     *
     * @param key
     * @param string
     */
    public synchronized void append(String key, String string)
    {
        append(key, new ByteArrayInputStream(string.getBytes()));
    }

    /**
     * 这个key所有的文件，按照index的顺序，整合成同一个流
     *
     * @param key
     * @return
     */
    public synchronized InputStream get(final String key)
    {
        SequenceInputStream inputStream = null;
        File files[] = dir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.isFile() && pathname.getName().startsWith(key + ".");
            }
        });
        if (files != null)
        {
            try
            {
                List<File> fileList = Arrays.asList(files);
                Collections.sort(fileList);
                Vector<InputStream> vector = new Vector<>();
                for (File file : fileList)
                {
                    FileInputStream fis = new FileInputStream(file);
                    vector.add(fis);
                }
                Enumeration<InputStream> e = vector.elements();
                inputStream = new SequenceInputStream(e);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        return inputStream;
    }

    /**
     * 获取指定index的key的流
     *
     * @param key
     * @param index
     * @return
     */
    public synchronized InputStream get(String key, int index)
    {
        InputStream inputStream = null;
        File file = new File(dir, key + "." + index);
        if (file.exists() && file.isFile())
        {
            try
            {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        return inputStream;
    }

    /**
     * 已知文件内容是文字的情况下，直接转换成文字返回
     *
     * @param key
     * @param index
     * @return
     */
    public synchronized String getString(String key, int index)
    {
        String result = null;
        InputStream in = get(key, index);
        if (in != null)
        {
            try
            {
                LinkedList<Byte> linkedList = new LinkedList<>();
                byte b = (byte) in.read();
                while (b != -1)
                {
                    linkedList.add(b);
                    b = (byte) in.read();
                }
                byte buff[] = new byte[linkedList.size()];
                for (int a = 0; a < linkedList.size(); a++)
                {
                    buff[a] = linkedList.get(a);
                }
                linkedList.clear();
                result = new String(buff);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 删除指定key的所有index文件
     *
     * @param key
     */
    public synchronized void delete(final String key)
    {
        File files[] = dir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.isFile() && pathname.getName().startsWith(key + ".");
            }
        });
        for (File file : files)
        {
            file.delete();
        }
    }

    /**
     * 删除指定key的，指定index的文件
     *
     * @param key
     * @param index
     */
    public synchronized void delete(String key, int index)
    {
        File file = new File(dir, key + "." + index);
        if (file.exists() && file.isFile())
        {
            file.delete();
        }
    }

    /**
     * 清除所有文件
     */
    public synchronized void clean()
    {
        File files[] = dir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.isFile();
            }
        });
        if (files != null)
        {
            for (File file : files)
            {
                file.delete();
            }
        }
    }

    /**
     * 获取这个key下，一共有几个index
     *
     * @param key
     * @return
     */
    public synchronized int[] getKeyIndexs(final String key)
    {
        int indexs[] = new int[0];
        File files[] = dir.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.isFile() && pathname.getName().split("\\.")[0].equals(key);
            }
        });

        if (files != null)
        {
            indexs = new int[files.length];
            for (int a = 0; a < files.length; a++)
            {
                File file = files[a];
                String index = file.getName().split("\\.")[1];
                indexs[a] = Integer.parseInt(index);
            }
        }

        return indexs;
    }

    public synchronized static DiskCache open(File dir, String version)
    {
        if (diskCache == null)
        {
            diskCache = new DiskCache(dir, version);
        }
        return diskCache;
    }

    /**
     * 把流写入新建文件
     *
     * @param file
     * @param in
     */
    private void createFileByStream(File file, InputStream in)
    {
        try
        {
            if (!file.exists())
            {
                boolean createResult = file.createNewFile();
                System.out.println("createResult:" + createResult);
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte buff[] = new byte[1024];
            int len = 0;
            while ((len = in.read(buff)) > 0)
            {
                fos.write(buff, 0, len);
            }
            fos.flush();
            fos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
