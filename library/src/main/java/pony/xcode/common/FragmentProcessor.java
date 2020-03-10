package pony.xcode.common;


import android.text.TextUtils;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import pony.xcode.base.R;

public class FragmentProcessor {

    private FragmentManager mFragmentManager;
    private List<Fragment> mFragments;

    private FragmentProcessor(AppCompatActivity activity) {
        mFragmentManager = activity.getSupportFragmentManager();
    }

    public static FragmentProcessor from(AppCompatActivity activity) {
        return new FragmentProcessor(activity);
    }

    public void startFragment(@IdRes int containerId, Fragment fragment, @Nullable String tag, @AnimatorRes @AnimRes int enterAnim, @AnimatorRes @AnimRes int outAnim) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (enterAnim != 0 || outAnim != 0) {
            transaction.setCustomAnimations(enterAnim, outAnim);
        }
        if (!TextUtils.isEmpty(tag)) {
            transaction.add(containerId, fragment, tag);
        } else {
            transaction.add(containerId, fragment);
        }
        transaction.commitAllowingStateLoss();
        addFragmentToList(fragment);
    }

    public void addFragmentToList(Fragment fragment) {
        if (mFragments == null) {
            mFragments = new ArrayList<>();
        }
        mFragments.add(fragment);
    }

    /*remove fragment*/
    public void removeFragment(Fragment fragment) {
        removeFragment(fragment, 0, R.anim.fragment_out_right);
    }

    /*remove fragment with anim*/
    public void removeFragment(Fragment fragment, @AnimatorRes @AnimRes int enterAnim, @AnimatorRes @AnimRes int outAnim) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (enterAnim != 0 || outAnim != 0) {
            transaction.setCustomAnimations(enterAnim, outAnim);
        }
        transaction.remove(fragment).commitAllowingStateLoss();
        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStack();
        }
        removeFragmentFromList(fragment);
    }

    public void removeFragmentFromList(Fragment fragment) {
        if (mFragments != null) {
            mFragments.remove(fragment);
        }
    }

    public boolean canHandleBack() {
        if (mFragments == null || mFragments.size() <= 1) {  //当前activity没有添加任何fragment或者只添加一个fragment，则直接结束当前activity
            return true;
        } else {
            Fragment fragment = mFragments.get(mFragments.size() - 1);
            removeFragment(fragment);
            return false;
        }
    }

    /*获取当前activity中addFragment or replaceFragment fragment总数*/
    @NonNull
    public List<Fragment> getFragments() {
        if (mFragments == null) return new ArrayList<>();
        return mFragments;
    }
}
