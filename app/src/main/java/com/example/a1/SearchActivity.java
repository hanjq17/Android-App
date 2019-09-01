package com.example.a1;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private NewsDatabaseManager newsDatabaseManager;
    private FragmentManager fManager;
    private ArrayList<Fragment> fragments=new ArrayList<>();
    private SearchView mSearchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //引用menu文件
        getMenuInflater().inflate(R.menu.real_search, menu);

        //找到SearchView并配置相关参数
        MenuItem searchItem = menu.findItem(R.id.real_search1);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setSearchResults(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(fragments.get(0)!=null){
                    ((SearchHint)fragments.get(0)).changeSearchText(newText);
                }
                return false;
            }
        });

        //搜索图标是否显示在搜索框内
        mSearchView.setIconifiedByDefault(true);
        //设置搜索框展开时是否显示提交按钮，可不显示
        mSearchView.setSubmitButtonEnabled(true);
        //让键盘的回车键设置成搜索
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        //搜索框是否展开，false表示展开
        mSearchView.setIconified(false);
        //获取焦点
        mSearchView.setFocusable(true);
        mSearchView.requestFocusFromTouch();
        //设置提示词
        mSearchView.setQueryHint("请输入关键字");
        //设置输入框文字颜色
        EditText editText = (EditText) mSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
        editText.setHintTextColor(ContextCompat.getColor(this, R.color.gray));
        editText.setTextColor(ContextCompat.getColor(this, R.color.bg_white));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        newsDatabaseManager=NewsDatabaseManager.getInstance(this);
        setContentView(R.layout.activity_search);
        Toolbar tb=findViewById(R.id.toolbar2);
        setSupportActionBar(tb);
        getSupportActionBar().setTitle("");
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        for(int i=0;i<2;i++){
            fragments.add(null);
        }
        fManager=getSupportFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        fragments.set(0,new SearchHint(this,""));
        fTransaction.add(R.id.search_frame,fragments.get(0));
        fTransaction.commit();

    }

    public void setSearchResults(String keywords){
        newsDatabaseManager.addQueryMessage(keywords);
        FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        fragments.set(1,new SearchPage(this,keywords));
        fTransaction.add(R.id.search_frame,fragments.get(1));
        fTransaction.commit();
        mSearchView.setQueryHint(keywords);
        mSearchView.clearFocus();
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        for(Fragment f:fragments){
            if(f!=null) fragmentTransaction.hide(f);
        }
    }
}
