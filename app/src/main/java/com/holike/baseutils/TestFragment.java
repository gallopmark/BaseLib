package com.holike.baseutils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.vectordrawable.graphics.drawable.AnimatorInflaterCompat;

import java.util.Random;

import pony.xcode.base.ViewPagerFragment;

/**
 * Created by pony on 2019/11/15.
 * Version v3.0 app报表
 */
public class TestFragment extends ViewPagerFragment implements View.OnClickListener {

    private String[] testArray = new String[]{"one", "two", "three", "four", "five", "six"};
    private int[] mResIds = new int[]{R.id.iv_clickable, R.id.iv_a, R.id.iv_b, R.id.iv_c, R.id.iv_d};
    private ImageView[] mImageViews = new ImageView[mResIds.length];
    private boolean mFlag = true;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_test;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        Toolbar toolbar = mContentView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        int sum = mResIds.length;
        for (int i = 0; i < sum; i++) {
            ImageView imageView = mContentView.findViewById(mResIds[i]);
            imageView.setOnClickListener(this);
            mImageViews[i] = imageView;
        }
    }

    @Override
    protected void startLoad() {
        Log.e("test", testArray[new Random().nextInt(testArray.length)] + "load start...");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_clickable) {
            if (mFlag) {
                startAnim();
            } else {
                closeAnim();
            }
        }
    }

    private void closeAnim() {
        AnimatorSet animator = (AnimatorSet) AnimatorInflaterCompat.loadAnimator(mContext,R.animator.close_anim);
        animator.setDuration(2000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
//        ObjectAnimator animator0 = ObjectAnimator.ofFloat(mImageViews[0], "alpha", 1f, 0.5f);
//        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mImageViews[1], "translationY", 200f, 0f);
//        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mImageViews[2], "translationX", 200f, 0f);
//        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mImageViews[3], "translationY", -200f, 0f);
//        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mImageViews[4], "translationX", -200f, 0f);
//        AnimatorSet set = new AnimatorSet();
//        set.setInterpolator(new DecelerateInterpolator());
//        set.setDuration(1000);
//        set.playTogether(animator0, animator1, animator2, animator3, animator4);
//        set.start();
        mFlag = true;
    }

    private void startAnim() {
        ObjectAnimator animator0 = ObjectAnimator.ofFloat(mImageViews[0], "alpha", 0.5f, 1f);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mImageViews[1], "translationY", 200f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mImageViews[2], "translationX", 200f);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mImageViews[3], "translationY", -200f);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mImageViews[4], "translationX", -200f);
        AnimatorSet set = new AnimatorSet();
        set.setInterpolator(new DecelerateInterpolator());
        set.setDuration(1000);
        set.playTogether(animator0, animator1, animator2, animator3, animator4);
        set.start();
        mFlag = false;
    }
}
