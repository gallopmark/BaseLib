package com.holike.baseutils;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import pony.xcode.base.WebViewActivity;
import pony.xcode.widget.TitleBar;

/**
 * Created by pony on 2019/11/20.
 * Version v3.0 app报表
 */
public class SecondActivity extends WebViewActivity {
    TitleBar mTitleBar;

    @Nullable
    @Override
    protected View getCustomTitleView() {
        View view = LayoutInflater.from(this).inflate(R.layout.include_titlebar, mWebContent, false);
        mTitleBar = view.findViewById(R.id.toolbar);
        mTitleBar.setTitle(getExtraTitle());
        return view;
    }

    @Nullable
    @Override
    protected View getErrorView() {
        View errorView = LayoutInflater.from(this).inflate(R.layout.include_h5_load_error, mWebContainer, false);
        Button btRetry = errorView.findViewById(R.id.bt_retry);
        btRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        return errorView;
    }

    @Override
    protected void onReceivedTitle(String title) {
        mTitleBar.setTitle(title);
    }
}
