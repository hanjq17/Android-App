package com.example.a1;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import android.view.Menu;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{
    //Fragment Object

    int a=0;
    private FragmentManager fManager;
    private ArrayList<Fragment> fragments=new ArrayList<>();
    private Context mContext;
    private NewsDatabaseManager newsDatabaseManager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fTransaction = fManager.beginTransaction();
            hideAllFragment(fTransaction);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(fragments.get(0) == null){
                        fragments.set(0,new NewsPage(mContext));
                        fTransaction.add(R.id.ly_content,fragments.get(0));
                    }else{
                        fTransaction.show(fragments.get(0));
                        fragments.get(0).setUserVisibleHint(true);
                    }
                    fTransaction.commit();
                    return true;
                case R.id.navigation_dashboard:
                    if(fragments.get(1) == null){
                        fragments.set(1,new RecommandPage(mContext));
                        fTransaction.add(R.id.ly_content,fragments.get(1));
                    }else{
                        fTransaction.show(fragments.get(1));
                    }
                    fTransaction.commit();
                    return true;
                case R.id.navigation_notifications:
                    if(fragments.get(2) == null){
                        fragments.set(2,new MyPage(MainActivity.this));
                        fTransaction.add(R.id.ly_content,fragments.get(2));
                    }else{
                        fTransaction.show(fragments.get(2));
                    }
                    fTransaction.commit();
                    return true;
            }
            return false;
        }
    };

    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        for(Fragment f:fragments){
            if(f!=null) fragmentTransaction.hide(f);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);


        //找到SearchView并配置相关参数

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent it=new Intent(MainActivity.this,SearchActivity.class);
                startActivity(it);
                return false;
            }
        });
        MenuItem changeItem = menu.findItem(R.id.change_user);
        changeItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                newsDatabaseManager.logout();
                Intent it=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(it);
                finish();
                return false;
            }
        });
        MenuItem nightMode = menu.findItem(R.id.night_mode);
        if(NewsDatabaseManager.style==1){
            nightMode.setTitle("白天模式");
        }



        nightMode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //TODO

                if(NewsDatabaseManager.style==0){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    newsDatabaseManager.setStyle(1);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    newsDatabaseManager.setStyle(0);
                }
                finish();
                Intent intent=new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        setContentView(R.layout.activity_main);
        Toolbar tb=findViewById(R.id.toolbar_main);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("");
        newsDatabaseManager=NewsDatabaseManager.getInstance(this);
        newsDatabaseManager.setUser();
        if(NewsDatabaseManager.currentUser.length()==0){
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

        mContext=this;
        for(int i=0;i<3;i++){
            fragments.add(null);
        }
        Bridge.setSystemCacheDir(getCacheDir());
        fManager=getSupportFragmentManager();
        BottomNavigationView navView = findViewById(R.id.botNav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        fragments.set(0,new NewsPage(mContext));
        fTransaction.add(R.id.ly_content,fragments.get(0));
        fTransaction.commit();
        
        NavigationView navigationView = findViewById(R.id.nav_view);
        View tmpv=navigationView.inflateHeaderView(R.layout.nav_header_main);
        TextView name_text=tmpv.findViewById(R.id.name_header);
        name_text.setText(NewsDatabaseManager.currentUser);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public void onBackPressed() {
        Log.d("asd","asd");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.his) {
            Intent it=new Intent(this,HistoryActivity.class);
            it.putExtra("type","history");
            startActivity(it);

        } else if (id == R.id.fav) {
            Intent it=new Intent(this,HistoryActivity.class);
            it.putExtra("type","favorite");
            startActivity(it);

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

/*
public class MainActivity extends FragmentActivity implements
        NavigationView.OnNavigationItemSelectedListener, RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener {
    private FragmentManager fManager;
    private ViewPager vpager;
    private NewsFragmentPagerAdapter mAdapter;
    private RadioGroup rg_tab_bar;
    private RadioButton rb_channel;
    private RadioButton rb_message;
    private RadioButton rb_better;
    private RadioButton rb_setting;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fTransaction = fManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_dashboard:

                    return true;
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fManager = getSupportFragmentManager();
        mAdapter = new NewsFragmentPagerAdapter(getSupportFragmentManager(),this);
        bindViews();
        rb_channel.setChecked(true);
        BottomNavigationView navView = findViewById(R.id.botNav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_channel:
                vpager.setCurrentItem(0);
                break;
            case R.id.rb_message:
                vpager.setCurrentItem(1);
                break;
            case R.id.rb_better:
                vpager.setCurrentItem(2);
                break;
            case R.id.rb_setting:
                vpager.setCurrentItem(3);
                break;
        }
    }

    private void bindViews() {
        rg_tab_bar = (RadioGroup) findViewById(R.id.rg_tab_bar);
        rb_channel = (RadioButton) findViewById(R.id.rb_channel);
        rb_message = (RadioButton) findViewById(R.id.rb_message);
        rb_better = (RadioButton) findViewById(R.id.rb_better);
        rb_setting = (RadioButton) findViewById(R.id.rb_setting);
        rg_tab_bar.setOnCheckedChangeListener(this);

        vpager = (ViewPager) findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.setCurrentItem(0);
        vpager.addOnPageChangeListener(this);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {


        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case 0:
                    rb_channel.setChecked(true);
                    break;
                case 1:
                    rb_message.setChecked(true);
                    break;
                case 2:
                    rb_better.setChecked(true);
                    break;
                case 3:
                    rb_setting.setChecked(true);
                    break;
            }
        }
    }
}
*/
