package pony.xcode.base;

import pony.xcode.mvp.BasePresenter;
import pony.xcode.mvp.BaseView;
import pony.xcode.utils.GenericsUtils;

/*mvp*/
public abstract class PresenterActivity<P extends BasePresenter, V extends BaseView> extends CommonActivity {

    protected P mPresenter;

    @SuppressWarnings("unchecked")
    @Override
    void applyPresenter() {
        try {
            mPresenter = ((Class<P>) GenericsUtils.getGenericsSuperclassType(getClass())).newInstance();
            if (this instanceof BaseView) {
                mPresenter.register((V) this);
            }
        } catch (Exception e) {
            //You should extends CommonActivity only
        }
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.unregister();
        }
        super.onDestroy();
    }
}
