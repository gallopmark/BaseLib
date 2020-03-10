package com.holike.baseutils;


import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import pony.xcode.base.CommonPopupWindow;
import pony.xcode.common.FragmentSwitcher;
import pony.xcode.system.ToastCompat;
import pony.xcode.base.PresenterActivity;
import pony.xcode.widget.TitleBar;

public class MainActivity extends PresenterActivity<MainPresenter, MainView> implements MainView {
    private TabLayout tabLayout;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean isFullScreen() {
        return false;
    }

    @Override
    protected void setScreenStyle() {
        setTheme(R.style.LightStatusBarTheme);
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        TitleBar titleBar = findViewById(R.id.toolbar);
        titleBar.inflateMenu(R.menu.menu_main);
        titleBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_share) {
                    ToastCompat.makeText(MainActivity.this, "分享成功", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
        tabLayout = findViewById(R.id.tabLayout);
        recomputeTlOffset1(tabLayout.getTabCount() - 4, getResources().getDimensionPixelOffset(R.dimen.dp_80));
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MainFragment());
        fragments.add(new TestFragment());
        fragments.add(new TestFragment());
        fragments.add(new TestFragment());
        fragments.add(new TestFragment());
        fragments.add(new TestFragment());
        final FragmentSwitcher helper = FragmentSwitcher.obtain(R.id.container, getSupportFragmentManager());
        helper.setup(fragments);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                helper.setCurrentTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        TextView tvShowPopup = findViewById(R.id.tv_show_popup);
        tvShowPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyPopupWindow(MainActivity.this).showAsDropDown(view);
            }
        });
        mPresenter.getData();
    }

    private void recomputeTlOffset1(final int index, int viewWidth) {
        if (tabLayout.getTabAt(index) != null) tabLayout.getTabAt(index).select();
        //加上半个item的宽度（这个需要自己微调，不一定是半个）如果有设置margin还需要加上margin的距离
        int halfWidth = viewWidth / 2; //偏移量
        final int width = (viewWidth * index) + halfWidth;
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
//                tabLayout.smoothScrollTo(width, 0);
                tabLayout.setScrollPosition(index, 0, true);
            }
        });
    }

    @Override
    public void onSuccess(String json) {
        ToastCompat.makeText(this, json, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(String failReason) {

    }

    class MyPopupWindow extends CommonPopupWindow {

        MyPopupWindow(Context context) {
            super(context);
        }

        @Override
        public int getWidth() {
            return ViewGroup.LayoutParams.MATCH_PARENT;
        }

        @Override
        public int getBottomMargin() {
            return getResources().getDimensionPixelSize(R.dimen.dp_44);
        }

        @Override
        protected int getLayoutResourceId() {
            return R.layout.layout_popup_show;
        }
    }

    private class MyAdapter extends FragmentPagerAdapter {
        List<Fragment> fragments;

        MyAdapter(@NonNull FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
