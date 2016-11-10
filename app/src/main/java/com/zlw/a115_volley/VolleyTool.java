package com.zlw.a115_volley;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.File;

/**
 * Created by zlw on 2016/11/5 0005.
 */
public class VolleyTool {
    private static VolleyTool volleySingleton;   //VolleyTool单例
    private RequestQueue mRequestQueue; //请求队列(单例)
    private ImageLoader mImageLoader;
    private Context mContext;

    public VolleyTool(Context context) {
        this.mContext = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public VolleyTool(Context context, HttpStack stack) {
        this.mContext = context;
        mRequestQueue = getRequestQueue(context, stack);
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }


    public static synchronized VolleyTool getInstance(Context context, boolean isDiskCatch) {
        if (isDiskCatch) {
            if (volleySingleton == null) {
                volleySingleton = new VolleyTool(context, null);
            }
        } else {
            if (volleySingleton == null) {
                volleySingleton = new VolleyTool(context);
            }
        }

        return volleySingleton;
    }


    public static synchronized VolleyTool getInstance(Context context, HttpStack stack) {
        if (volleySingleton == null) {
            volleySingleton = new VolleyTool(context, stack);
        }
        return volleySingleton;
    }

    public static synchronized VolleyTool getInstance(Context context) {
        if (volleySingleton == null) {
            volleySingleton = new VolleyTool(context);
        }
        return volleySingleton;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public RequestQueue getRequestQueue(Context context, HttpStack stack) {
        File cacheDir = new File(context.getCacheDir(), "http_cache");
        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        if (stack == null) {
            stack = new HurlStack();
        }
        Network network = new BasicNetwork(stack);
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        queue.start();
        return queue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}