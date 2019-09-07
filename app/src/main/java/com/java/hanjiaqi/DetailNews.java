package com.java.hanjiaqi;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.ShareConstant;
import com.xyzlf.share.library.util.ShareUtil;

import java.util.ArrayList;
import java.util.Locale;

import cn.jzvd.JzvdStd;
import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;





public class DetailNews extends AppCompatActivity implements View.OnClickListener{

    private View sim_news;
    private boolean isFav=false;
    private String newsID,titleStr,contentStr,timeStr,publisherStr,keywordsStr,type,video,url;
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
    private SpeechSynthesizer mTts;




    class MySynthesizerListener implements SynthesizerListener {

        @Override
        public void onSpeakBegin() {
            showTip(" 开始播放 ");
        }

        @Override
        public void onSpeakPaused() {
            showTip(" 暂停播放 ");
        }

        @Override
        public void onSpeakResumed() {
            showTip(" 继续播放 ");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos ,
                                     String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成 ");
            } else if (error != null ) {
                showTip(error.getPlainDescription( true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1 , int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话 id，当业务出错时将会话 id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话 id为null
            //if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //     String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //     Log.d(TAG, "session id =" + sid);
            //}
        }


    }

    private void showTip (String data) {
        Toast.makeText( this, data, Toast.LENGTH_SHORT).show() ;
    }

    private String getResourcePath(){
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/xiaoyan.jet"));
        return tempBuffer.toString();
    }








    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_news);



        mTts = SpeechSynthesizer.createSynthesizer( this, null);
        /*mTts.setParameter(ResourceUtil.TTS_RES_PATH,getResourcePath());
        mTts.setParameter(SpeechConstant. VOICE_NAME, "xiaoyan" ); // 设置发音人
        mTts.setParameter(SpeechConstant. SPEED, "50" );// 设置语速
        mTts.setParameter(SpeechConstant. VOLUME, "80" );// 设置音量，范围 0~100
        mTts.setParameter(SpeechConstant. ENGINE_TYPE, SpeechConstant. TYPE_CLOUD); //设置云端
        mTts.setParameter(SpeechConstant.SAMPLE_RATE,"8000");
        mTts.setParameter(SpeechConstant. TTS_AUDIO_PATH, "./sdcard/iflytek.pcm" );*/
        Toolbar tb=findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("新闻详情");
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
        video=it.getStringExtra("video");
        url=it.getStringExtra("url");
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
        if(video.length()>0){
            View videoView=View.inflate(this,R.layout.detail_video,null);
            JzvdStd jzvdStd = (JzvdStd) videoView.findViewById(R.id.detail_video_jz);
            jzvdStd.setUp(video,titleStr);
            linearLayout.addView(videoView);
        }
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
            for(int i=0;i<images.size()-contents.length;i++){
                View image=View.inflate(this,R.layout.detai_image,null);
                ImageView imageView=image.findViewById(R.id.detail_imageView);
                imageLoader.displayImage(images.get(i),imageView,options);
                linearLayout.addView(image);
            }
            for(int i=0;i<contents.length;i++){
                View text=View.inflate(this,R.layout.detai_text,null),
                        image=View.inflate(this,R.layout.detai_image,null);
                TextView textView=text.findViewById(R.id.detail_content);
                ImageView imageView=image.findViewById(R.id.detail_imageView);
                textView.setText(contents[i]);
                imageLoader.displayImage(images.get(contents.length+i),imageView,options);
                linearLayout.addView(image);linearLayout.addView(text);
            }
        }
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //if(mTts.isSpeaking())mTts.stopSpeaking();
                if(!type.equals("Main")) {
                    Intent itnt = new Intent(DetailNews.this, HistoryActivity.class);
                    itnt.putExtra("type", type);
                    startActivity(itnt);
                }
            }
        });



    }

    @Override
    public void onBackPressed() {
        finish();
        //if(mTts.isSpeaking())mTts.stopSpeaking();
        if(!type.equals("Main")) {
            Intent itnt = new Intent(DetailNews.this, HistoryActivity.class);
            itnt.putExtra("type", type);
            startActivity(itnt);
        }

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

                ShareEntity testBean = new ShareEntity(titleStr, titleStr);
                testBean.setUrl(url); //分享链接
                if(images.size()!=0) testBean.setImgUrl(images.get(0));
                ShareUtil.showShareDialog(this,  testBean, ShareConstant.REQUEST_CODE);




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
                    mes.addImages(images);
                    mes.addVideo(video);
                    manager.add(mes,"favorite");
                }
                isFav=!isFav;
                if(isFav)item.setTitle("取消收藏");
                else item.setTitle("收藏");
                return true;
            case R.id.speak:
                //Log.d("speak","123123123");
                if(mTts.isSpeaking()){
                    mTts.stopSpeaking();
                    item.setTitle("朗读");
                }
                else{
                    item.setTitle("结束");
                    mTts.startSpeaking( titleStr+"。"+contentStr, new MySynthesizerListener());
                }
                return true;
            case R.id.dislike:
                //Log.d("speak","123123123");
                String topword=null;double topscore=0;
                String[] keywords=keywordsStr.split("\\|");
                for(String keyword:keywords){
                    if(keyword.equals("")) continue;
                    String[] wordAndScore=keyword.split(",");
                    double tmpscore=Double.parseDouble(wordAndScore[1]);
                    if(tmpscore>topscore){
                        topscore=tmpscore;
                        topword=wordAndScore[0];
                    }
                }
                if(topword!=null) {
                    NewsDatabaseManager.getInstance(this).addBanWord(topword);
                    Toast.makeText(this,"屏蔽成功，将减少\""+topword+"\"相关新闻的推送",Toast.LENGTH_SHORT).show();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}