package com.holike.baseutils;

import pony.xcode.mvp.BaseView;

/**
 * Created by pony on 2019/11/18.
 * Version v3.0 app报表
 */
public interface MainView extends BaseView {

    void onSuccess(String json);

    void onFailure(String failReason);
}
