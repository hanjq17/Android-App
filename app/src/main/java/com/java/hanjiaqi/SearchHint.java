package com.java.hanjiaqi;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class SearchHint extends Fragment {

    private Context father;
    private NewsDatabaseManager newsDatabaseManager;
    private LinearLayout newsList;
    private String searching;
    public SearchHint(Context context,String searching){
        father=context;
        this.searching=searching;
        newsDatabaseManager=NewsDatabaseManager.getInstance(father);
    }

    public void changeSearchText(String searching){
        this.searching=searching;
        newsList.removeAllViews();
        ArrayList<String> results=newsDatabaseManager.selectAllQueryMessages(this.searching,5);
        for(String result:results){
            addItem(result);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_hint, container, false);
        newsList = view.findViewById(R.id.search_hints);
        newsList.removeAllViews();
        ArrayList<String> results=newsDatabaseManager.selectAllQueryMessages(searching,5);
        for(String result:results){
            addItem(result);
        }
        return view;
    }

    public void addItem(final String result){
        final View vw=View.inflate(father,R.layout.sim_search_hint,null);
        TextView textView=vw.findViewById(R.id.sim_search_hint);
        Button button=vw.findViewById(R.id.search_cancel);
        Button search_button=vw.findViewById(R.id.search_icon);
        textView.setText(result);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SearchActivity)getActivity()).setSearchResults(result);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsDatabaseManager.delQueryMessage(result);
                newsList.removeView(vw);
            }
        });
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SearchActivity)getActivity()).setSearchResults(result);
            }
        });
        newsList.addView(vw);
    }
}
