package com.example.a1;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    //@StringRes
    //private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};

    private ArrayList<String> titles=new ArrayList<>();
    private NewsDatabaseManager newsDatabaseManager;
    private ArrayList<HomePage> homePages=new ArrayList<>();
    private final Context mContext;
    private int idOffset;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        newsDatabaseManager=NewsDatabaseManager.getInstance(mContext);
        resetTitles();
    }

    public void resetTitles(){
        idOffset+=getCount()+1;
        ArrayList<ChannelItem> selected=newsDatabaseManager.selectItemByStatus(1);
        ArrayList<HomePage> tmp=new ArrayList<>();
        ArrayList<String> tt=new ArrayList<>();
        for(ChannelItem item:selected){
            tt.add(item.name);
            tmp.add(new HomePage(mContext,item.name));
        }
        titles=tt;homePages=tmp;
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        Log.d("idpos",position+"");
        return position+idOffset;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Log.d("pos",position+"");
        return homePages.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return titles.size();
    }
}