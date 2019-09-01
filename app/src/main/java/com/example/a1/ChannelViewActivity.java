package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import com.cheng.channel.Channel;
import com.cheng.channel.ChannelView;

public class ChannelViewActivity extends AppCompatActivity implements ChannelView.OnChannelListener {
    private String TAG = getClass().getSimpleName();
    private ChannelView channelView;
    private NewsDatabaseManager newsDatabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_view);

        Toolbar tb=findViewById(R.id.toolbar3);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("");
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChannelViewActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        newsDatabaseManager=NewsDatabaseManager.getInstance(this);
        channelView = findViewById(R.id.channelView);

        init();
    }

    private void init() {

        ArrayList<ChannelItem> channelItems=newsDatabaseManager.selectItemByStatus(1);
        ArrayList<Channel> channels1=new ArrayList<>();
        for(int i=0;i<channelItems.size();i++){
            Channel channel=new Channel(channelItems.get(i).name,1,0);
            channels1.add(channel);
        }

        channelItems=newsDatabaseManager.selectItemByStatus(0);
        ArrayList<Channel> channels2=new ArrayList<>();
        for(int i=0;i<channelItems.size();i++){
            Channel channel=new Channel(channelItems.get(i).name,2,0);
            channels2.add(channel);
        }

        channelView.setChannelFixedCount(1);
        channelView.addPlate("已选内容",channels1);
        channelView.addPlate("待选内容",channels2);
        channelView.inflateData();
        channelView.setOnChannelItemClickListener(this);
    }


    @Override
    public void channelItemClick(int position, Channel channel) {
        Log.d("111","111");
        Log.i(TAG, position + ".." + channel);
    }

    @Override
    public void channelEditFinish(List<Channel> channelList) {
        Log.d("222","222");
        Log.i(TAG, channelList.toString());
        Log.i(TAG, channelView.getMyChannel().toString());
        newsDatabaseManager.delAllItems();
        ArrayList<ChannelItem> channelItems=new ArrayList<>();
        for(int i=0;i<channelList.size();i++){
            channelItems.add(new ChannelItem(i,channelList.get(i).getChannelName(),i,1));
        }
        newsDatabaseManager.addGridItems(channelItems,1);
        Intent intent=new Intent(this,MainActivity.class);
    }

    @Override
    public void channelEditStart() {

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}