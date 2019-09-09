package com.java.hanjiaqi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.cheng.channel.ChannelView;
import com.google.android.material.tabs.TabLayout;

public class NewsPage extends Fragment {

    private Context father;
    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ImageView imageView;
    TabLayout tabs;
    public NewsPage(Context context){
        father=context;
        NewsDatabaseManager newsDatabaseManager=NewsDatabaseManager.getInstance(father);
        if(newsDatabaseManager.selectItemByStatus(1).size()==0) {
            newsDatabaseManager.setDefault();
            Log.d("111","222");
        }
    }

    public NewsPage(){
        father=getActivity();
        NewsDatabaseManager newsDatabaseManager=NewsDatabaseManager.getInstance(father);
        if(newsDatabaseManager.selectItemByStatus(1).size()==0) {
            newsDatabaseManager.setDefault();
            Log.d("111","222");
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.d("curr: ","666");
        if(viewPager!=null){
            Log.d("curr: ",viewPager.getCurrentItem()+"");
            sectionsPagerAdapter.getItem(viewPager.getCurrentItem()).setUserVisibleHint(true);
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_page, container, false);
        sectionsPagerAdapter = new SectionsPagerAdapter(father,getFragmentManager());
        viewPager = view.findViewById(R.id.view_pager2);
        viewPager.setOffscreenPageLimit(sectionsPagerAdapter.getCount()-1);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        imageView=view.findViewById(R.id.add);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(father, ChannelViewActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }

}
