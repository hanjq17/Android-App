package com.example.a1;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

public class DetailNews extends AppCompatActivity implements View.OnClickListener {

    private View sim_news;
    private boolean isFav=false;
    private String newsID,titleStr,contentStr,timeStr,publisherStr,keywordsStr,type;

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
        TextView content=(TextView)findViewById(R.id.detail_content);
        content.setText(contentStr);
        timeStr=((Intent) it).getStringExtra("time");
        publisherStr=((Intent) it).getStringExtra("publisher");
        TextView message=(TextView)findViewById(R.id.detail_message);
        message.setText(publisherStr+" "+timeStr);
        isFav=it.getBooleanExtra("favorite",false);
        newsID=it.getStringExtra("newsID");
        keywordsStr=it.getStringExtra("keywords");
        type=it.getStringExtra("type");
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