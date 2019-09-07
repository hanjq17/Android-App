package com.example.a1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.jzvd.JzvdStd;

public class RecommandPage extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private Context father;
    private LinearLayout newsList;
    private TextView loading;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsDatabaseManager newsDatabaseManager;
    private String lastTime;
    private Integer notOverNum;
    private ArrayList<NewsMessage> news=new ArrayList<>();
    private ScrollView scrollView;
    private ImageLoader imageLoader=ImageLoader.getInstance();
    private boolean isLoading=false;
    private Toast toast;
    final int num=10;


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

    public RecommandPage(Context context) {
        father=context;
        newsDatabaseManager=NewsDatabaseManager.getInstance(context);
        toast= Toast.makeText(father,"Loading",Toast.LENGTH_SHORT);
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
            jzvdStd.setUp(news.getVideo(),news.getTitle());
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(newsList!=null && news.size()>0){
            newsList.removeAllViews();
            ArrayList<String> banWords=newsDatabaseManager.selectBanWords();
            boolean visible=true;
            for(NewsMessage newsMessage:news) {
                visible = true;
                for (KeyWord keyWord : newsMessage.getKeyWords()) {
                    if (banWords.contains(keyWord.word)) {
                        visible = false;
                        break;
                    }
                }
                if (visible) addItem(newsMessage);
            }
        }
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
        final SimpleDateFormat tmp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(lastTime==null) lastTime=tmp.format(new Date());
        final ArrayList<String> urls=new ArrayList<>();
        ArrayList<KeyWord> keyWords=newsDatabaseManager.selectKeywords(3);
        if(keyWords.size()==0){
            urls.add("https://api2.newsminer.net/svc/news/queryNewsList?size=10&endDate="+lastTime);
        }
        else{
            double sum=0.0;
            for(KeyWord kw:keyWords) sum+=kw.score;
            int num=10;
            for(int i=0;i<keyWords.size()-1;i++){
                int size=(int)(10*keyWords.get(i).score/sum);num-=size;
                urls.add("https://api2.newsminer.net/svc/news/queryNewsList?size="+size+"&endDate="+lastTime+"&words="+keyWords.get(i).word);
            }
            urls.add("https://api2.newsminer.net/svc/news/queryNewsList?size="+num+"&endDate="+lastTime+"&words="+keyWords.get(keyWords.size()-1).word);
        }
        notOverNum=urls.size();
        for(int i=0;i<urls.size();i++){
            final String url=urls.get(i);
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    NewsFetcher fetcher=new NewsFetcher();
                    final ArrayList<NewsMessage> messages=fetcher.getNews(url);
                    for(NewsMessage mes:messages){
                        synchronized (lastTime) {
                            if(mes.getTime().compareTo(lastTime)<0){
                                lastTime = mes.getTime();
                            }
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
                                synchronized (notOverNum){
                                    notOverNum--;
                                    if(notOverNum==0){
                                        newsList.removeView(loading);
                                    }
                                }
                                ArrayList<String> banWords=newsDatabaseManager.selectBanWords();
                                boolean visible;
                                for (int i = 0; i < messages.size(); ++i) {
                                    synchronized (newsList){
                                        visible=true;
                                        for(KeyWord keyWord:messages.get(i).getKeyWords()){
                                            if(banWords.contains(keyWord.word)) visible=false;
                                        }
                                        if(visible)addItem(messages.get(i));
                                        news.add(messages.get(i));
                                    }
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
        return view;
    }

    @Override
    public void onRefresh() {
        newsList.addView(loading,0);
        final SimpleDateFormat tmp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(lastTime==null) lastTime=tmp.format(new Date());
        final ArrayList<String> urls=new ArrayList<>();
        ArrayList<KeyWord> keyWords=newsDatabaseManager.selectKeywords(3);
        if(keyWords.size()==0){
            urls.add("https://api2.newsminer.net/svc/news/queryNewsList?size=10&endDate="+lastTime);
        }
        else{
            double sum=0.0;
            for(KeyWord kw:keyWords) sum+=kw.score;
            int num=10;
            for(int i=0;i<keyWords.size()-1;i++){
                int size=(int)(10*keyWords.get(i).score/sum);num-=size;
                urls.add("https://api2.newsminer.net/svc/news/queryNewsList?size="+size+"&endDate="+lastTime+"&words="+keyWords.get(i).word);
            }
            urls.add("https://api2.newsminer.net/svc/news/queryNewsList?size="+num+"&endDate="+lastTime+"&words="+keyWords.get(keyWords.size()-1).word);

        }
        notOverNum=urls.size();
        for(int i=0;i<urls.size();i++){
            final String url=urls.get(i);
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    NewsFetcher fetcher=new NewsFetcher();
                    final ArrayList<NewsMessage> messages=fetcher.getNews(url);
                    for(NewsMessage mes:messages){
                        synchronized (lastTime) {
                            if(mes.getTime().compareTo(lastTime)<0){
                                lastTime = mes.getTime();
                            }
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
                                synchronized (notOverNum){
                                    notOverNum--;
                                    if(notOverNum==0){
                                        newsList.removeView(loading);
                                        handlerForRefresh.sendEmptyMessage(0x93);
                                    }
                                }
                                ArrayList<String> banWords=newsDatabaseManager.selectBanWords();
                                boolean visible;
                                for (int i = 0; i < messages.size(); ++i) {
                                    synchronized (newsList){
                                        visible=true;
                                        for(KeyWord keyWord:messages.get(i).getKeyWords()){
                                            if(banWords.contains(keyWord.word)) visible=false;
                                        }
                                        if(visible)addItem(messages.get(i),0);
                                        news.add(0,messages.get(i));
                                    }
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


    public void bottomRefresh() {
        if(isLoading==true) return;
        toast.show();
        isLoading=true;
        final SimpleDateFormat tmp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(lastTime==null) lastTime=tmp.format(new Date());
        final ArrayList<String> urls=new ArrayList<>();
        ArrayList<KeyWord> keyWords=newsDatabaseManager.selectKeywords(3);
        if(keyWords.size()==0){
            urls.add("https://api2.newsminer.net/svc/news/queryNewsList?size=10&endDate="+lastTime);
        }
        else{
            double sum=0.0;
            for(KeyWord kw:keyWords) sum+=kw.score;
            int num=10;
            for(int i=0;i<keyWords.size()-1;i++){
                int size=(int)(10*keyWords.get(i).score/sum);num-=size;
                urls.add("https://api2.newsminer.net/svc/news/queryNewsList?size="+size+"&endDate="+lastTime+"&words="+keyWords.get(i).word);
            }
            urls.add("https://api2.newsminer.net/svc/news/queryNewsList?size="+num+"&endDate="+lastTime+"&words="+keyWords.get(keyWords.size()-1).word);
        }
        notOverNum=urls.size();
        for(int i=0;i<urls.size();i++){
            final String url=urls.get(i);
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    NewsFetcher fetcher=new NewsFetcher();
                    final ArrayList<NewsMessage> messages=fetcher.getNews(url);
                    for(NewsMessage mes:messages){
                        synchronized (lastTime) {
                            if(mes.getTime().compareTo(lastTime)<0){
                                lastTime = mes.getTime();
                            }
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
                                synchronized (notOverNum){
                                    notOverNum--;
                                    if(notOverNum==0){
                                        toast.cancel();
                                        isLoading=false;
                                        newsList.removeView(loading);
                                    }
                                }
                                ArrayList<String> banWords=newsDatabaseManager.selectBanWords();
                                boolean visible;
                                for (int i = 0; i < messages.size(); ++i) {
                                    synchronized (newsList){
                                        visible=true;
                                        for(KeyWord keyWord:messages.get(i).getKeyWords()){
                                            if(banWords.contains(keyWord.word)) visible=false;
                                        }
                                        if(visible)addItem(messages.get(i));
                                        news.add(messages.get(i));
                                    }
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

    @Override
    public void onDestroy() {
        imageLoader.clearMemoryCache();
        super.onDestroy();
    }
}
