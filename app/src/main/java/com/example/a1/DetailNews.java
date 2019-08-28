package com.example.a1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailNews extends Activity implements View.OnClickListener {
    @Override
    public void onClick(View view) {


    }
    public void forFinish(){
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detail_news);
        Intent it=getIntent();
        String content=((Intent) it).getStringExtra("newsNum");
        TextView tv=(TextView)findViewById(R.id.textView2);
        tv.setText(content);
        Button bt=(Button)findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forFinish();
            }
        });
    }
}