package com.java.hanjiaqi;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyPage extends Fragment {
    long loginTime;
    Context context;
    NewsDatabaseManager newsDatabaseManager;
    TextView tv2;
    MyPage(Context context){
        this.context=context;
        newsDatabaseManager=NewsDatabaseManager.getInstance(context);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(tv2!=null)tv2.setText("当前屏蔽的关键词：\n"+String.join(",",newsDatabaseManager.selectBanWords()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_page, container, false);
        NewsDatabaseManager manager=NewsDatabaseManager.getInstance(context);
        loginTime=manager.getlogintime();

        TextView tv=view.findViewById(R.id.mypage_username);tv.setText("我的账户： "+NewsDatabaseManager.currentUser);
        SimpleDateFormat tmp=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt=new Date();dt.setTime(loginTime);
        TextView tv1=view.findViewById(R.id.mypage_logintime);tv1.setText("上次登录时间： "+tmp.format(dt));
        Button banbutton=view.findViewById(R.id.mypage_buttonban);
        Button disbanbutton=view.findViewById(R.id.mypage_buttondisban);
        EditText editText1=view.findViewById(R.id.mypage_banword);
        EditText editText2=view.findViewById(R.id.mypage_disbanword);
        tv2=view.findViewById(R.id.mypage_alreadyban);
        tv2.setText("当前屏蔽的关键词：\n"+String.join(",",newsDatabaseManager.selectBanWords()));
        banbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String forban=editText1.getText().toString();
                // add forban to database
                newsDatabaseManager.addBanWord(forban);
                tv2.setText("当前屏蔽的关键词：\n"+String.join(",",newsDatabaseManager.selectBanWords()));
                //modify tv2 add this word
            }
        });
        disbanbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fordisban=editText2.getText().toString();
                // dis add disforban to database
                newsDatabaseManager.delBanWord(fordisban);
                tv2.setText("当前屏蔽的关键词：\n"+String.join(",",newsDatabaseManager.selectBanWords()));
                //modify tv2 dis add  this word
            }
        });




        return view;
    }
}
