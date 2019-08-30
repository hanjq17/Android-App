package com.example.a1;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class NewsPage extends Fragment {

    private Context father;
    ViewPager viewPager;
    public NewsPage(Context context){
        father=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_page, container, false);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(father,getFragmentManager());
        viewPager = view.findViewById(R.id.view_pager2);
        viewPager.setOffscreenPageLimit(sectionsPagerAdapter.getCount()-1);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        return view;
    }
}
