package pony.xcode.mvp;

import androidx.annotation.Nullable;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseModel implements IModel {

    private CompositeDisposable mDisposables;

    public void addDisposable(@Nullable Disposable disposable) {
        if (disposable != null) {
            if (mDisposables == null) {
                mDisposables = new CompositeDisposable();
            }
            mDisposables.add(disposable);
        }
    }

    public void removeDisposable(@Nullable Disposable disposable) {
        if (disposable != null) {
            if (mDisposables != null) {
                mDisposables.remove(disposable);
            }
        }
    }

    public void clear() {
        if (mDisposables != null) {
            mDisposables.clear();
        }
    }

    @Override
    public void destroy() {
        if (mDisposables != null) {
            mDisposables.dispose();
        }
    }
}
