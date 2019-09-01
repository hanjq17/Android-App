package com.example.a1;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class AppContext extends Application {



    /**

     * Called when the application is starting, before any activity, service, or

     * receiver objects (excluding content providers) have been created.

     * （当应用启动的时候，会在任何activity、Service或者接收器被创建之前调用，所以在这里进行ImageLoader 的配置）

     * 当前类需要在清单配置文件里面的application下进行name属性的设置。

     */

    @Override

    public void onCreate() {

        // TODO Auto-generated method stub

        super.onCreate();

        // 缓存图片的配置，一般通用的配置

        initImageLoader(getApplicationContext());

    }



    private void initImageLoader(Context context) {

        // TODO Auto-generated method stub

        // 创建DisplayImageOptions对象

        DisplayImageOptions defaulOptions = new DisplayImageOptions.Builder()

                .cacheInMemory(true).cacheOnDisk(true).build();

        // 创建ImageLoaderConfiguration对象

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(

                context).defaultDisplayImageOptions(defaulOptions)

                .threadPriority(Thread.NORM_PRIORITY - 2)

                .denyCacheImageMultipleSizesInMemory()

                .diskCacheFileNameGenerator(new Md5FileNameGenerator())

                .tasksProcessingOrder(QueueProcessingType.LIFO).build();

        // ImageLoader对象的配置

        ImageLoader.getInstance().init(configuration);

    }



}