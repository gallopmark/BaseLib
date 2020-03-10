package pony.xcode.mvp;


import androidx.annotation.Nullable;

interface IPresenter<V extends BaseView> {
    /**
     * 注册View层
     */
    void register(V view);

    /**
     * 获取View
     */
    @Nullable
    V getView();

    /**
     * 销毁动作（如Activity、Fragment的卸载）
     */
    void unregister();
}
