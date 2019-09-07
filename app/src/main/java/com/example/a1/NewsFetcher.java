package com.example.a1;

import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class NewsFetcher {

    public NewsFetcher(){
    }
    

    public ArrayList<NewsMessage> getNews(String u) {
        try {
            Log.d("URL",u);
            //使用该地址创建一个 URL 对象
            URL url = new URL(u);
            //使用创建的URL对象的openConnection()方法创建一个HttpURLConnection对象
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            Log.d("A","C");
            /**
             * 设置HttpURLConnection对象的参数
             */
            // 设置请求方法为 GET 请求
            httpURLConnection.setRequestMethod("GET");
            Log.d("A","B");
            //使用输入流
            httpURLConnection.setDoInput(true);
            //GET 方式，不需要使用输出流
            httpURLConnection.setDoOutput(false);
            //设置超时
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);
            //连接
            Log.d("A","D");
            httpURLConnection.connect();
            Log.d("A","A");
            //还有很多参数设置 请自行查阅
            //连接后，创建一个输入流来读取response
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            //每次读取一行，若非空则添加至 stringBuilder
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            //读取所有的数据后，赋值给 response
            final String response = stringBuilder.toString().trim();
            Log.d("Title",response);

            try {
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("data");
                ArrayList<NewsMessage> newsMessages=new ArrayList<>();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject js=jsonArray.getJSONObject(i);
                    String time=js.getString("publishTime");
                    String title=js.getString("title");
                    String content=js.getString("content");
                    String publisher=js.getString("publisher");
                    String newsID=js.getString("newsID");
                    String allimages=js.getString("image");

                    Log.d("want",allimages.length()+"");
                    String video=js.getString("video");
                    String newsurl=js.getString("url");

                    if(allimages.length()!=0&&allimages.charAt(0)=='['){
                        allimages=allimages.substring(1,allimages.length()-1);
                    }


                    String[] images=allimages.split(", ");
                    NewsMessage newsMessage=new NewsMessage(title,content,time,publisher,newsID);
                    newsMessage.addVideo(video);
                    newsMessage.addUrl(newsurl);
                    for(String image:images) {
                        if(!image.equals(""))
                        newsMessage.addImage(image);
                    }
                    JSONArray keywords=js.getJSONArray("keywords");
                    for(int j=0;j<keywords.length();j++){
                        JSONObject kw=keywords.getJSONObject(j);
                        String word=kw.getString("word");
                        double score=kw.getDouble("score");
                        newsMessage.addKeyword(word,score);
                    }
                    newsMessages.add(newsMessage);
                }
                return newsMessages;
            }catch(Exception e){
                e.printStackTrace();
            }
            try {
                bufferedReader.close();
            }catch (Exception e){}
            httpURLConnection.disconnect();
        }catch (Exception e){
            Log.d("qwqwq","wwqwwq");
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
