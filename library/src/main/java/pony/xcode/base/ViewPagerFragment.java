package pony.xcode.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/*ViewPager +Fragment实现数据懒加载*/
@SuppressWarnings("deprecation")
public abstract class ViewPagerFragment extends CommonFragment {

    private boolean mDoLazyLoadLater = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getUserVisibleHint()) {
            onLazyLoad();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onLazyLoad();
        }
    }

    private void onLazyLoad() {
        if (!mDoLazyLoadLater && mViewInflated) {
            startLoad();
            mDoLazyLoadLater = true;
        }
    }

    protected void startLoad() {

    }
}
