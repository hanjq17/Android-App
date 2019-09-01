package com.example.a1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SearchPage extends Fragment {
    private Context father;
    private LinearLayout newsList;
    private TextView loading;
    private NewsDatabaseManager newsDatabaseManager;
    private String keywords;
    private ScrollView scrollView;
    private String lastTime;
    final int num=10;
    ArrayList<NewsMessage> messages;
    boolean isLoading=true;
    Toast toast;

    public SearchPage(Context context,String keywords) {
        father=context;
        this.keywords=keywords;
        newsDatabaseManager=NewsDatabaseManager.getInstance(context);
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
        View vw=View.inflate(father,R.layout.sim_news,null);
        TextView title=(TextView)vw.findViewById(R.id.textView);
        TextView othermes=(TextView)vw.findViewById(R.id.textView3);
        title.setText(news.getTitle());
        othermes.setText(news.getPublisher()+" "+news.getTime());
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
        View vw=View.inflate(father,R.layout.sim_news,null);
        TextView title=(TextView)vw.findViewById(R.id.textView);
        TextView othermes=(TextView)vw.findViewById(R.id.textView3);
        title.setText(news.getTitle());
        othermes.setText(news.getPublisher()+" "+news.getTime());
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
