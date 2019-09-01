package com.example.a1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Inflater;

public class HomePage extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private Context father;
    private LinearLayout newsList;
    private TextView loading;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsDatabaseManager newsDatabaseManager;
    private ImageLoader imageLoader;
    private String type;
    private ScrollView scrollView;
    private String lastTime;
    final int num=10;
    boolean needSet=false;
    boolean completed=false;
    ArrayList<NewsMessage> messages;
    boolean isLoading=true;
    Toast toast;

    Handler handlerForRefresh = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x93: {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        }
    };

    public HomePage(Context context,String type) {
        father=context;
        this.type=type;
        newsDatabaseManager=NewsDatabaseManager.getInstance(context);
        imageLoader=ImageLoader.getInstance();
        toast=Toast.makeText(father,"Loading",Toast.LENGTH_SHORT);
    }

    public void startDetailNews(NewsMessage news){
        String newsID=news.getID();
        if(newsDatabaseManager.existsID(newsID,"history")) {
            newsDatabaseManager.update(newsID,"history");
        }
        else {
            newsDatabaseManager.add(news,"history");
            newsDatabaseManager.addKeywords(news.getKeyWords());
        }
        Intent intent=new Intent(father,DetailNews.class);
        intent.putExtra("title",news.getTitle());
        intent.putExtra("publisher",news.getPublisher());
        intent.putExtra("time",news.getTime());
        intent.putExtra("content",news.getContent());
        intent.putExtra("newsID",newsID);
        intent.putExtra("keywords",news.getStringKeyWords());
        intent.putExtra("type","Main");
        intent.putExtra("images",news.getStringImages());
        if(newsDatabaseManager.existsID(newsID,"favorite")) intent.putExtra("favorite",true);
        else intent.putExtra("favorite",false);
        startActivity(intent);
    }

    public void addItem(final NewsMessage news){
        View vw;
        if(news.getImages().size()==0) vw=View.inflate(father,R.layout.sim_news,null);
        else if(news.getImages().size()>=3) vw=View.inflate(father,R.layout.sim_news_three_pic,null);
        else vw=View.inflate(father,R.layout.sim_news_one_pic,null);
        TextView title=(TextView)vw.findViewById(R.id.textView);
        TextView othermes=(TextView)vw.findViewById(R.id.textView3);
        title.setText(news.getTitle());
        othermes.setText(news.getPublisher()+" "+news.getTime());
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher_foreground)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.ic_launcher_foreground)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_launcher_foreground)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(20))// 设置成圆角图片
                .build();// 创建DisplayImageOptions对象
        if(news.getImages().size()>=3){
            ImageView pic1=vw.findViewById(R.id.imageView1),pic2=vw.findViewById(R.id.imageView2),pic3=vw.findViewById(R.id.imageView3);
            imageLoader.displayImage(news.getImages().get(0), pic1, options);
            imageLoader.displayImage(news.getImages().get(1), pic2, options);
            imageLoader.displayImage(news.getImages().get(2), pic3, options);
        }
        else if(news.getImages().size()>0){
            ImageView pic1=vw.findViewById(R.id.imageView);
            imageLoader.displayImage(news.getImages().get(0), pic1, options);
        }
        if(newsDatabaseManager.existsID(news.getID(),"history")){
            title.setTextColor(Color.GRAY);
            othermes.setTextColor(Color.GRAY);
        }
        vw.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TextView title1=(TextView)view.findViewById(R.id.textView);
                TextView othermes1=(TextView)view.findViewById(R.id.textView3);
                title1.setTextColor(Color.GRAY);
                othermes1.setTextColor(Color.GRAY);
                startDetailNews(news);
            }
        });
        //TODO
        newsList.addView(vw);
    }

    public void addItem(final NewsMessage news,final int index){
        View vw;
        if(news.getImages().size()==0) vw=View.inflate(father,R.layout.sim_news,null);
        else if(news.getImages().size()>=3) vw=View.inflate(father,R.layout.sim_news_three_pic,null);
        else vw=View.inflate(father,R.layout.sim_news_one_pic,null);
        TextView title=(TextView)vw.findViewById(R.id.textView);
        TextView othermes=(TextView)vw.findViewById(R.id.textView3);
        title.setText(news.getTitle());
        othermes.setText(news.getPublisher()+" "+news.getTime());
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_launcher_foreground)// 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.drawable.ic_launcher_foreground)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.ic_launcher_foreground)// 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(20))// 设置成圆角图片
                .build();// 创建DisplayImageOptions对象
        if(news.getImages().size()>=3){
            ImageView pic1=vw.findViewById(R.id.imageView1),pic2=vw.findViewById(R.id.imageView2),pic3=vw.findViewById(R.id.imageView3);
            imageLoader.displayImage(news.getImages().get(0), pic1, options);
            imageLoader.displayImage(news.getImages().get(1), pic2, options);
            imageLoader.displayImage(news.getImages().get(2), pic3, options);
        }
        else if(news.getImages().size()>0){
            ImageView pic1=vw.findViewById(R.id.imageView);
            imageLoader.displayImage(news.getImages().get(0), pic1, options);
        }
        if(newsDatabaseManager.existsID(news.getID(),"history")){
            title.setTextColor(Color.GRAY);
            othermes.setTextColor(Color.GRAY);
        }
        vw.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TextView title1=(TextView)view.findViewById(R.id.textView);
                TextView othermes1=(TextView)view.findViewById(R.id.textView3);
                title1.setTextColor(Color.GRAY);
                othermes1.setTextColor(Color.GRAY);
                startDetailNews(news);
            }
        });
        //TODO
        newsList.addView(vw,index);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d("Position","setUserVisibleHint");
        super.setUserVisibleHint(isVisibleToUser);
        if(completed) return;
        if(!isVisibleToUser) return;
        if(newsList==null){
            needSet=true;
            return;
        }
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                NewsFetcher fetcher=new NewsFetcher();
                SimpleDateFormat tmp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if(lastTime==null) lastTime=tmp.format(new Date());
                if(type.equals("最新")) {
                    messages=fetcher.getNews("https://api2.newsminer.net/svc/news/queryNewsList?size=10&endDate="+lastTime);
                }
                else {
                    messages = fetcher.getNews("https://api2.newsminer.net/svc/news/queryNewsList?size=10&endDate=" + lastTime + "&categories=" + type);
                }
                for(NewsMessage mes:messages){
                    if(mes.getTime().compareTo(lastTime)<0){
                        lastTime=mes.getTime();
                    }
                }
                try {
                    Date dt=tmp.parse(lastTime);
                    dt.setTime(dt.getTime()-1000);
                    lastTime=tmp.format(dt);
                } catch (ParseException e) {
                }

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            newsList.removeView(loading);
                            isLoading=false;
                            for (int i = 0; i < messages.size(); ++i) {
                                addItem(messages.get(i));
                            }
                            completed = true;
                        }
                    });
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Position","onCreateView");
        View view = inflater.inflate(R.layout.home_page, container, false);
        newsList = view.findViewById(R.id.list);
        loading=view.findViewById(R.id.loading);
        swipeRefreshLayout=view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        scrollView=view.findViewById(R.id.news_scroll);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()!=MotionEvent.ACTION_UP) return false;
                if(scrollView.getChildAt(scrollView.getChildCount()-1).getHeight()- scrollView.getHeight()== scrollView.getScrollY()){
                    bottomRefresh();
                }
                return false;
            }
        });
        if(needSet){
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    NewsFetcher fetcher=new NewsFetcher();
                    SimpleDateFormat tmp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if(lastTime==null) lastTime=tmp.format(new Date());
                    if(type.equals("最新")) {
                        messages=fetcher.getNews("https://api2.newsminer.net/svc/news/queryNewsList?size=10&endDate="+lastTime);
                    }
                    else {
                        messages = fetcher.getNews("https://api2.newsminer.net/svc/news/queryNewsList?size=10&endDate=" + lastTime + "&categories=" + type);
                    }
                    for(NewsMessage mes:messages){
                        if(mes.getTime().compareTo(lastTime)<0){
                            lastTime=mes.getTime();
                        }
                    }
                    try {
                        Date dt=tmp.parse(lastTime);
                        dt.setTime(dt.getTime()-1000);
                        lastTime=tmp.format(dt);
                    } catch (ParseException e) {
                    }
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newsList.removeView(loading);
                                isLoading=false;
                                for (int i = 0; i < messages.size(); ++i) {
                                    addItem(messages.get(i));
                                }
                                completed = true;
                            }
                        });
                    }catch(NullPointerException e){
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        return view;
    }

    @Override
    public void onRefresh() {
        if(isLoading==true) {
            handlerForRefresh.sendEmptyMessage(0x93);
            return;
        }
        newsList.addView(loading,0);
        isLoading=true;
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                NewsFetcher fetcher=new NewsFetcher();
                SimpleDateFormat tmp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if(lastTime==null) lastTime=tmp.format(new Date());
                if(type.equals("最新")) {
                    messages=fetcher.getNews("https://api2.newsminer.net/svc/news/queryNewsList?size=10&endDate="+lastTime);
                }
                else {
                    messages = fetcher.getNews("https://api2.newsminer.net/svc/news/queryNewsList?size=10&endDate=" + lastTime + "&categories=" + type);
                }
                for(NewsMessage mes:messages){
                    if(mes.getTime().compareTo(lastTime)<0){
                        lastTime=mes.getTime();
                    }
                }
                try {
                    Date dt=tmp.parse(lastTime);
                    dt.setTime(dt.getTime()-1000);
                    lastTime=tmp.format(dt);
                } catch (ParseException e) {
                }
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            newsList.removeView(loading);
                            isLoading=false;
                            for (int i = 0; i < messages.size(); ++i) {
                                addItem(messages.get(i),i);
                            }
                            handlerForRefresh.sendEmptyMessage(0x93);
                        }
                    });
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void bottomRefresh() {
        if(isLoading==true) return;
        toast.show();
        isLoading=true;
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                NewsFetcher fetcher=new NewsFetcher();
                SimpleDateFormat tmp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if(lastTime==null) lastTime=tmp.format(new Date());
                if(type.equals("最新")) {
                    messages=fetcher.getNews("https://api2.newsminer.net/svc/news/queryNewsList?size=10&endDate="+lastTime);
                }
                else {
                    messages = fetcher.getNews("https://api2.newsminer.net/svc/news/queryNewsList?size=10&endDate=" + lastTime + "&categories=" + type);
                }
                for(NewsMessage mes:messages){
                    if(mes.getTime().compareTo(lastTime)<0){
                        lastTime=mes.getTime();
                    }
                }
                try {
                    Date dt=tmp.parse(lastTime);
                    dt.setTime(dt.getTime()-1000);
                    lastTime=tmp.format(dt);
                } catch (ParseException e) {
                }
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                            isLoading=false;
                            for (int i = 0; i < messages.size(); ++i) {
                                addItem(messages.get(i));
                            }
                        }
                    });
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public void onDestroy() {
        imageLoader.clearMemoryCache();
        super.onDestroy();
    }
}


