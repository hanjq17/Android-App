package com.java.hanjiaqi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.jzvd.JzvdStd;

public class SearchPage extends Fragment {
    private Context father;
    private LinearLayout newsList;
    private TextView loading;
    private NewsDatabaseManager newsDatabaseManager;
    private String keywords;
    private ScrollView scrollView;
    private String lastTime;
    private ImageLoader imageLoader=ImageLoader.getInstance();
    final int num=10;
    ArrayList<NewsMessage> messages;
    boolean isLoading=false;
    private boolean connMark=true;
    private View disconnectView;
    private ArrayList<NewsMessage> news=new ArrayList<>();
    Toast toast;

    public SearchPage(Context context,String keywords) {
        father=context;
        this.keywords=keywords;
        newsDatabaseManager=NewsDatabaseManager.getInstance(context);
        toast=Toast.makeText(father,"Loading",Toast.LENGTH_SHORT);
        disconnectView=View.inflate(context,R.layout.disconnect,null);
    }

    private boolean isConnected(){
        if (father != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) father.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
                return true;//有网
            }
        }
        return false;
    }


    private void checkConnected(){
        if(newsList==null) return;
        Log.d("NetInfo: ",isConnected()+"");
        if(!isConnected()){
            Log.d("NetInfo: ","disconnected");
            newsList.removeAllViews();
            newsList.addView(disconnectView);
            connMark=false;
            isLoading=false;
        }
        else if(!connMark){
            connMark=true;
            newsList.removeAllViews();
            if(news.size()>0) {
                for(NewsMessage newsMessage:news){
                    addItem(newsMessage);
                }
                return;
            }
        }
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
        intent.putExtra("type","Search");
        intent.putExtra("images",news.getStringImages());
        intent.putExtra("video",news.getVideo());
        intent.putExtra("url",news.getUrl());
        if(newsDatabaseManager.existsID(newsID,"favorite")) intent.putExtra("favorite",true);
        else intent.putExtra("favorite",false);
        startActivity(intent);
    }

    public void addItem(final NewsMessage news){
        View vw;
        if(!news.getVideo().equals("")) vw=View.inflate(father,R.layout.sim_news_video,null);
        else if(news.getImages().size()==0) vw=View.inflate(father,R.layout.sim_news,null);
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
        if(!news.getVideo().equals("")){
            JzvdStd jzvdStd = (JzvdStd) vw.findViewById(R.id.jz_video);
            jzvdStd.setUp(news.getVideo()
                    , news.getTitle());
        }

        else if(news.getImages().size()>=3){
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
        if(!news.getVideo().equals("")) vw=View.inflate(father,R.layout.sim_news_video,null);
        else if(news.getImages().size()==0) vw=View.inflate(father,R.layout.sim_news,null);
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

        if(!news.getVideo().equals("")){
            JzvdStd jzvdStd = (JzvdStd) vw.findViewById(R.id.jz_video);
            jzvdStd.setUp(news.getVideo()
                    , news.getTitle());
        }

        else if(news.getImages().size()>=3){
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Position","onCreateSearchView");
        View view = inflater.inflate(R.layout.search_result, container, false);
        newsList = view.findViewById(R.id.search_list);
        loading=view.findViewById(R.id.search_loading);
        scrollView=view.findViewById(R.id.search_scroll);
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
        checkConnected();
        if(!connMark) return view;
        isLoading=true;
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                NewsFetcher fetcher=new NewsFetcher();
                SimpleDateFormat tmp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if(lastTime==null) lastTime=tmp.format(new Date());
                messages=fetcher.getNews("https://api2.newsminer.net/svc/news/queryNewsList?size=10&endDate="+lastTime+"&words="+keywords);
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
                                news.add(messages.get(i));
                            }
                        }
                    });
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        return view;
    }


    public void bottomRefresh() {
        if(isLoading==true) return;
        toast.show();
        isLoading=true;
        checkConnected();
        if(!connMark) return;
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                NewsFetcher fetcher=new NewsFetcher();
                SimpleDateFormat tmp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if(lastTime==null) lastTime=tmp.format(new Date());
                messages=fetcher.getNews("https://api2.newsminer.net/svc/news/queryNewsList?size=10&endDate="+lastTime+"&words="+keywords);
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
                                news.add(messages.get(i));
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
}
