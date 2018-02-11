package com.xiaolei.okhttputil.Catch.CacheImpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.xiaolei.okhttputil.Catch.Interfaces.CacheInterface;
import com.xiaolei.okhttputil.Utils.Util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * 缓存到sqlite里的实现方式
 * Created by xiaolei on 2018/2/9.
 */

public class SqliteCacheImpl implements CacheInterface
{
    private static SqliteCacheImpl instance = null;
    private final String dbName = "ResponseStore.db";
    private SQLiteDatabase sqLiteDatabase;

    private String TextTab = "TextCacheTb";     // 文本
    private String BlobTab = "BlobCacheTb";     // 二进制表

    private SqliteCacheImpl(File cacheDir, Context context)
    {
        if (!cacheDir.exists())
        {
            boolean result = cacheDir.mkdirs();
        }
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(new File(cacheDir, dbName), null);
        String BlobCacheTb = "CREATE TABLE \"" + BlobTab + "\" (\"id\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\"key\"  TEXT NOT NULL,\"value\"  BLOB);";
        String TextCacheTb = "CREATE TABLE \"" + TextTab + "\" (\"id\"  INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\"key\"  TEXT NOT NULL,\"value\"  TEXT);";
        Cursor cursor = sqLiteDatabase.query("sqlite_master", new String[]{"name"}, "type='table'", null, null, null, "name");
        boolean hasCreateB = false;
        boolean hasCreateT = false;

        while (cursor.moveToNext())
        {
            String tbName = cursor.getString(cursor.getColumnIndex("name"));
            if (BlobTab.equals(tbName))
            {
                hasCreateB = true;
            } else if (TextTab.equals(tbName))
            {
                hasCreateT = true;
            }
        }
        cursor.close();

        if (!hasCreateB)
        {
            sqLiteDatabase.execSQL(BlobCacheTb);
        }
        if (!hasCreateT)
        {
            sqLiteDatabase.execSQL(TextCacheTb);
        }
    }

    public synchronized static SqliteCacheImpl getInstance(File cacheDir, Context context)
    {
        if (instance == null)
        {
            instance = new SqliteCacheImpl(cacheDir, context);
        }
        return instance;
    }

    @Override
    public void put(String key, String value)
    {
        key = Util.encryptMD5(key);

        sqLiteDatabase.execSQL("delete from " + TextTab + " where key= '" + key + "'");
        ContentValues values = new ContentValues();
        values.put("key", "" + key);
        values.put("value", "" + value);
        sqLiteDatabase.insert(TextTab, null, values);
    }

    @Override
    public void put(String key, InputStream inputStream)
    {
        key = Util.encryptMD5(key);
        byte buff[] = new byte[1024 * 100];//设置缓冲区为100K
        try
        {
            sqLiteDatabase.execSQL("delete from " + BlobTab + " where key= '" + key + "'");

            ContentValues nullValue = new ContentValues();
            nullValue.put("key", key);
            nullValue.put("value", "".getBytes());
            sqLiteDatabase.insert(BlobTab, null, nullValue);
            
            int len = 0;
            while ((len = inputStream.read(buff)) > 0)
            {
                ContentValues values = new ContentValues();
                byte lb[] = new byte[len];
                System.arraycopy(buff, 0, lb, 0, len);
                values.put("key", key);
                values.put("value", lb);
                sqLiteDatabase.insert(BlobTab, null, values);
                lb = null;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getString(String key)
    {
        key = Util.encryptMD5(key);
        String result = null;
        Cursor cursor = null;
        try
        {
            cursor = sqLiteDatabase.query(TextTab, new String[]{"value"}, "key='" + key + "'", null, null, null, null);
            while (cursor.moveToNext())
            {
                result = cursor.getString(cursor.getColumnIndex("value"));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            result = null;
        } finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
        return result;
    }


    @Override
    public InputStream getStream(String key)
    {
        key = Util.encryptMD5(key);
        InputStream inputStream = null;
        Cursor cursor = null;
        try
        {
            cursor = sqLiteDatabase.query(BlobTab, new String[]{"value"}, "key='" + key + "'", null, null, null, null);
            Vector<InputStream> vector = new Vector<>();
            while (cursor.moveToNext())
            {
                // 这里有一个隐患，就是当内存超级大的时候，内存会爆掉
                byte[] buff = cursor.getBlob(cursor.getColumnIndex("value"));
                vector.addElement(new ByteArrayInputStream(buff));
            }
            Enumeration<InputStream> e = vector.elements();
            inputStream = new SequenceInputStream(e);
        } catch (Exception e)
        {
            e.printStackTrace();
            inputStream = null;
        } finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
        return inputStream;
    }

    @Override
    public boolean containsKey(String key)
    {
        key = Util.encryptMD5(key);
        boolean hasContains = false;
        String checkText = "SELECT CASE WHEN c>0 THEN 'true' ELSE 'false' END as hasKey  FROM (SELECT count(*) as c FROM TextCacheTb WHERE key = '" + key + "');";
        String checkBlob = "SELECT CASE WHEN c>0 THEN 'true' ELSE 'false' END as hasKey  FROM (SELECT count(*) as c FROM BlobCacheTb WHERE key = '" + key + "');";

        Cursor tCursor = null;
        Cursor bCursor = null;

        try
        {
            tCursor = sqLiteDatabase.rawQuery(checkText, null);
            while (tCursor.moveToNext())
            {
                String hasKey = tCursor.getString(tCursor.getColumnIndex("hasKey"));
                if ("true".equals(hasKey))
                {
                    hasContains = true;
                }
            }
            if (!hasContains)
            {
                bCursor = sqLiteDatabase.rawQuery(checkBlob, null);
                while (bCursor.moveToNext())
                {
                    String hasKey = bCursor.getString(bCursor.getColumnIndex("hasKey"));
                    if ("true".equals(hasKey))
                    {
                        hasContains = true;
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            hasContains = false;
        } finally
        {
            if (tCursor != null)
            {
                tCursor.close();
            }
            if (bCursor != null)
            {
                bCursor.close();
            }
        }

        return hasContains;
    }
}
