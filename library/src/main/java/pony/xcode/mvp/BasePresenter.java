package pony.xcode.mvp;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

import pony.xcode.utils.GenericsUtils;

@SuppressWarnings("unchecked")
public abstract class BasePresenter<M extends BaseModel, V extends BaseView> implements IPresenter<V> {

    private WeakReference<V> mViewReference;
    protected M mModel;

    //非静态代码块会在每次类被调用或者被实例化时就会被执行。
    {
        try {
            mModel = ((Class<M>) GenericsUtils.getGenericsSuperclassType(getClass())).newInstance();
        } catch (Exception e) {
            //子类缺少范型参数的类型.
        }
    }

    @Override
    public void register(V view) {
        mViewReference = new WeakReference<>(view);
    }

    @Nullable
    public V getView() {
        if (mViewReference != null) {
            return mViewReference.get();
        }
        return null;
    }

    @Nullable
    protected M getModel() {
        return mModel;
    }

    //view是否绑定
    protected boolean isViewBind() {
        return mViewReference != null && mViewReference.get() != null;
    }

    @Override
    public void unregister() {
        if (mModel != null) {
            mModel.destroy();
        }
        unregisterView();
    }

    public void unregisterView() {
        if (mViewReference != null) {
            mViewReference.clear();
            mViewReference = null;
        }
    }
}
