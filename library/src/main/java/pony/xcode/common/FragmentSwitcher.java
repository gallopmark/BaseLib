package pony.xcode.common;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

/*fragment 切换管理*/
public class FragmentSwitcher {
    @IdRes
    private int mContainerViewId;
    private FragmentManager mFragmentManager;
    private List<? extends Fragment> mFragments;
    private String[] mTagArray;
    private Fragment mShowingFragment;
    private OnFragmentChangeListener mListener;

    private FragmentSwitcher(@IdRes int containerViewId, @NonNull FragmentManager fm) {
        this.mContainerViewId = containerViewId;
        this.mFragmentManager = fm;
    }

    public static FragmentSwitcher obtain(@IdRes int containerViewId, @NonNull FragmentManager fm) {
        return new FragmentSwitcher(containerViewId, fm);
    }

    public void setup(@NonNull List<? extends Fragment> fragments) {
        setup(fragments, 0);
    }

    public void setup(@NonNull List<? extends Fragment> fragments, int currentTab) {
        setup(fragments, null, currentTab);
    }

    public void setup(@NonNull List<? extends Fragment> fragments, @Nullable String[] tagArray) {
        setup(fragments, tagArray, 0); //默认展示第一个
    }

    public void setup(@NonNull List<? extends Fragment> fragments, @Nullable String[] tagArray, int currentTab) {
        if (tagArray == null) {
            tagArray = new String[fragments.size()];
            for (int i = 0; i < fragments.size(); i++) {
                tagArray[i] = fragments.get(i).getClass().getName();
            }
        }
        //tagArray的长度必须与fragments长度相等
        if (tagArray.length != fragments.size()) {
            throw new IllegalStateException("TagArray length must be the same as the fragments size !");
        }
        this.mFragments = fragments;
        this.mTagArray = tagArray;
        setCurrentTab(currentTab);
    }

    public void setCurrentTab(int position) {
        if (mFragments.isEmpty() || position < 0 || position >= mFragments.size()) {
            return;
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mShowingFragment != null) {
            transaction.hide(mShowingFragment);
        }
        String tag = mTagArray[position];
        Fragment fragment = mFragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            transaction.show(fragment);
        } else {
            fragment = mFragments.get(position);
            transaction.add(mContainerViewId, fragment, tag);
        }
        transaction.commitAllowingStateLoss();
        mShowingFragment = fragment;
        if (mListener != null) {
            mListener.onTabChange(position);
        }
    }

    public void setOnFragmentChangeListener(OnFragmentChangeListener listener) {
        this.mListener = listener;
    }

    public interface OnFragmentChangeListener {
        void onTabChange(int position);
    }
}
