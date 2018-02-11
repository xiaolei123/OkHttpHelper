# OkHttpHelper
an util of the okhttp

```
 implementation 'com.xiaolei:OkHttpUtil:1.1.1'
```


网络请求缓存的支持(get,post,一切，文字，图片，语音，文件，SQLite,自定义缓存目录)：

**CacheInterceptor.java**
```java
/**
 * 选择缓存方式，是缓存文件的方式，还是SQLite的方式
 */
public static enum Type
{
    FILE, SQLITE
}
```

Gzip网络数据压缩的支持：**GzipRequestInterceptor.java**

会话的支持：**SSessionInterceptor.java**

Cookie的本地持久化支持：**CookieJar.java**