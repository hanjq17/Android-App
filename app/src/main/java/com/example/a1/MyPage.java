package com.example.a1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyPage extends Fragment {
    String loginTime;
    Context context;
    MyPage(Context context){
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_page, container, false);
        NewsDatabaseManager manager=NewsDatabaseManager.getInstance(context);
        loginTime=manager.getlogintime();


        return view;
    }
}
