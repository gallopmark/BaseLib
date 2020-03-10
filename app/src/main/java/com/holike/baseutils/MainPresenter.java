package com.holike.baseutils;


import android.os.Handler;

import pony.xcode.mvp.BasePresenter;

public class MainPresenter extends BasePresenter<MainModel, MainView> {
    public void getData() {
        mModel.getData("", new MainModel.Callback() {
            @Override
            public void onHttpSuccess(final String json) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getView().onSuccess(json);
                    }
                }, 3000);
            }
        });
    }
}
