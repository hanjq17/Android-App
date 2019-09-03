package com.example.a1;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;

public class DetailNews extends AppCompatActivity implements View.OnClickListener {

    private View sim_news;
    private boolean isFav=false;
    private String newsID,titleStr,contentStr,timeStr,publisherStr,keywordsStr,type;
    private ArrayList<String> images=new ArrayList<>();
    private ImageLoader imageLoader=ImageLoader.getInstance();
    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_launcher_foreground)// 设置图片下载期间显示的图片
            .showImageForEmptyUri(R.drawable.ic_launcher_foreground)// 设置图片Uri为空或是错误的时候显示的图片
            .showImageOnFail(R.drawable.ic_launcher_foreground)// 设置图片加载或解码过程中发生错误显示的图片
            .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
            .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
            .displayer(new RoundedBitmapDisplayer(20))// 设置成圆角图片
            .build();// 创建DisplayImageOptions对象
    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_news);
        Toolbar tb=findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("");
        Intent it=getIntent();
        titleStr=((Intent) it).getStringExtra("title");
        TextView title=(TextView)findViewById(R.id.detail_title);
        title.setText(titleStr);
        contentStr=((Intent) it).getStringExtra("content");
        //TextView content=(TextView)findViewById(R.id.detail_content);
        //content.setText(contentStr);
        timeStr=((Intent) it).getStringExtra("time");
        publisherStr=((Intent) it).getStringExtra("publisher");
        TextView message=(TextView)findViewById(R.id.detail_message);
        message.setText(publisherStr+" "+timeStr);
        isFav=it.getBooleanExtra("favorite",false);
        newsID=it.getStringExtra("newsID");
        keywordsStr=it.getStringExtra("keywords");
        type=it.getStringExtra("type");
        String allImages=it.getStringExtra("images");
        String[] tmpimages=allImages.split("\\|");
        for(String image:tmpimages){
            if(!image.equals(""))
                images.add(image);
        }
        Log.d("imagenum",images.size()+"");
        LinearLayout linearLayout=findViewById(R.id.detail_layout);
        String[] contents=contentStr.split("\\n");
        Log.d("contentnum",contents.length+"");
        if(images.size()<=contents.length){
            for(int i=0;i<images.size();i++){
                View text=View.inflate(this,R.layout.detai_text,null),
                        image=View.inflate(this,R.layout.detai_image,null);
                TextView textView=text.findViewById(R.id.detail_content);
                ImageView imageView=image.findViewById(R.id.detail_imageView);
                textView.setText(contents[i]);
                imageLoader.displayImage(images.get(i),imageView,options);
                linearLayout.addView(text);linearLayout.addView(image);
            }
            for(int i=images.size();i<contents.length;i++){
                View text=View.inflate(this,R.layout.detai_text,null);
                TextView textView=text.findViewById(R.id.detail_content);
                textView.setText(contents[i]);
                linearLayout.addView(text);
            }
        }
        else{
            for(int i=0;i<contents.length;i++){
                View text=View.inflate(this,R.layout.detai_text,null),
                        image=View.inflate(this,R.layout.detai_image,null);
                TextView textView=text.findViewById(R.id.detail_content);
                ImageView imageView=image.findViewById(R.id.detail_imageView);
                textView.setText(contents[i]);
                imageLoader.displayImage(images.get(i),imageView,options);
                linearLayout.addView(text);linearLayout.addView(image);
            }
            for(int i=contents.length;i<images.size();i++){
                View image=View.inflate(this,R.layout.detai_image,null);
                ImageView imageView=image.findViewById(R.id.detail_imageView);
                imageLoader.displayImage(images.get(i),imageView,options);
                linearLayout.addView(image);
            }
        }
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if(!type.equals("Main")) {
                    Intent itnt = new Intent(DetailNews.this, HistoryActivity.class);
                    itnt.putExtra("type", type);
                    startActivity(itnt);
                }
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_news_menu,menu);
        MenuItem it=menu.findItem(R.id.favorite);
        if(isFav)it.setTitle("取消收藏");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.share:
                if(images.size()==0){

                    new Share(this,titleStr,null,false).share(this,titleStr,titleStr);
                }
                else {
                    new Share(this, titleStr, images.get(0),true).share(this,titleStr,titleStr);
                }
                //TODO
                return true;
            case R.id.favorite:
                NewsDatabaseManager manager=NewsDatabaseManager.getInstance(this);
                if(isFav){
                    manager.del(newsID,"favorite");
                }
                else{
                    NewsMessage mes=new NewsMessage(titleStr,contentStr,timeStr,publisherStr,newsID);
                    mes.addKeywords(keywordsStr);
                    manager.add(mes,"favorite");
                }
                isFav=!isFav;
                if(isFav)item.setTitle("取消收藏");
                else item.setTitle("收藏");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}