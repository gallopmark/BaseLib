package pony.xcode.recycler;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter<BaseRecyclerAdapter.RecyclerHolder> {
    protected Context mContext;
    protected LayoutInflater mLayoutInflater;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnItemChildClickListener mOnItemChildClickListener;
    private OnItemChildLongClickListener mOnItemChildLongClickListener;

    protected BaseRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
    }

    public interface OnItemClickListener {
        void onItemClick(BaseRecyclerAdapter adapter, RecyclerHolder holder, View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(BaseRecyclerAdapter adapter, RecyclerHolder holder, View view, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemChildClickListener {
        void onItemChildClick(BaseRecyclerAdapter adapter, RecyclerHolder holder, View view, int position);
    }

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.mOnItemChildClickListener = onItemChildClickListener;
    }

    public interface OnItemChildLongClickListener {
        void onItemChildLongClick(BaseRecyclerAdapter adapter, RecyclerHolder holder, View view, int position);
    }

    public void setOnItemChildLongClickListener(OnItemChildLongClickListener onItemChildLongClickListener) {
        this.mOnItemChildLongClickListener = onItemChildLongClickListener;
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private SparseArray<View> mViews;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);
            mViews = new SparseArray<>();
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @SuppressWarnings("unchecked")
        public final <V extends View> V getView(@IdRes int id) {
            View view = mViews.get(id);
            if (view == null) {
                view = itemView.findViewById(id);
                mViews.put(id, view);
            }
            return (V) view;
        }

        public void bindChildClick(@IdRes int id) {
            bindChildClick(getView(id));
        }

        /**
         * 子控件绑定局部点击事件
         */
        public void bindChildClick(View view) {
            view.setOnClickListener(this);
        }

        public void bindChildLongClick(@IdRes int id) {
            bindChildLongClick(getView(id));
        }

        public void bindChildLongClick(View view) {
            view.setOnLongClickListener(this);
        }

        /**
         * 文本控件赋值
         */
        public void setText(@IdRes int id, CharSequence text) {
            ((TextView) getView(id)).setText(text);
        }

        public void setTypeface(@IdRes int id, Typeface tf) {
            ((TextView) getView(id)).setTypeface(tf);
        }

        public void setDrawableLeft(@IdRes int id, @DrawableRes int resId) {
            setDrawableLeft(id, ContextCompat.getDrawable(mContext, resId));
        }

        public void setDrawableLeft(@IdRes int id, @Nullable Drawable drawable) {
            setDrawables(id, drawable, null, null, null);
        }

        public void setDrawableTop(@IdRes int id, @DrawableRes int resId) {
            setDrawableTop(id, ContextCompat.getDrawable(mContext, resId));
        }

        public void setDrawableTop(@IdRes int id, @Nullable Drawable drawable) {
            setDrawables(id, null, drawable, null, null);
        }

        public void setDrawableRight(@IdRes int id, @DrawableRes int resId) {
            setDrawableRight(id, ContextCompat.getDrawable(mContext, resId));
        }

        public void setDrawableRight(@IdRes int id, @Nullable Drawable drawable) {
            setDrawables(id, null, null, drawable, null);
        }

        public void setDrawableBottom(@IdRes int id, @DrawableRes int resId) {
            setDrawableBottom(id, ContextCompat.getDrawable(mContext, resId));
        }

        public void setDrawableBottom(@IdRes int id, @Nullable Drawable drawable) {
            setDrawables(id, null, null, null, drawable);
        }

        public void setDrawables(@IdRes int id, @DrawableRes int left, @DrawableRes int top, @DrawableRes int right, @DrawableRes int bottom) {
            ((TextView) getView(id)).setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        }

        public void setDrawables(@IdRes int id, @Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
            ((TextView) getView(id)).setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
        }

        public void setTextSize(@IdRes int id, float size) {
            ((TextView) getView(id)).setTextSize(size);
        }

        public void setTextSize(@IdRes int id, int unit, float size) {
            ((TextView) getView(id)).setTextSize(unit, size);
        }

        public void setTextSize(@IdRes int id, @DimenRes int dimen) {
            ((TextView) getView(id)).setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen);
        }

        //可以直接引用 R.color.xxx
        public void setTextColor(@IdRes int id, @ColorInt int color) {
            ((TextView) getView(id)).setTextColor(color);
        }

        public void setTextColorResource(@IdRes int id, @ColorRes int color) {
            ((TextView) getView(id)).setTextColor(ContextCompat.getColor(mContext, color));
        }

        public void setImageResource(@IdRes int id, @DrawableRes int resId) {
            ((ImageView) getView(id)).setImageResource(resId);
        }

        public void setVisibility(@IdRes int id, int visibility) {
            getView(id).setVisibility(visibility);
        }

        public void setVisibility(@IdRes int id, boolean isVisible) {
            getView(id).setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }

        public void setBackgroundColor(@IdRes int id, @ColorInt int color) {
            getView(id).setBackgroundColor(color);
        }

        public void setBackgroundColorResource(@IdRes int id, @ColorRes int color) {
            getView(id).setBackgroundColor(ContextCompat.getColor(mContext, color));
        }

        public void setBackgroundResource(@IdRes int id, @DrawableRes int resId) {
            getView(id).setBackgroundResource(resId);
        }

        public void setChecked(@IdRes int id, boolean isChecked) {
            if (getView(id) instanceof CheckBox) {
                ((CheckBox) getView(id)).setChecked(isChecked);
            } else if (getView(id) instanceof RadioButton) {
                ((RadioButton) getView(id)).setChecked(isChecked);
            } else {
                ((Checkable) getView(id)).setChecked(isChecked);
            }
        }

        public void setProgress(@IdRes int id, int progress) {
            ((ProgressBar) getView(id)).setProgress(progress);
        }

        public void setProgress(@IdRes int id, int progress, int max) {
            ProgressBar progressBar = getView(id);
            progressBar.setProgress(progress);
            progressBar.setMax(max);
        }

        public void setMax(@IdRes int id, int max) {
            ((ProgressBar) getView(id)).setMax(max);
        }

        public void setOnClickListener(@IdRes int id, View.OnClickListener onClickListener) {
            getView(id).setOnClickListener(onClickListener);
        }

        public void setLayoutParams(@IdRes int id, ViewGroup.LayoutParams params) {
            getView(id).setLayoutParams(params);
        }

        public void setEnabled(@IdRes int id, boolean isEnabled) {
            getView(id).setEnabled(isEnabled);
        }

        public void setVerticalLayoutManager(@IdRes int id) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            setLayoutManager(id, layoutManager);
        }

        public void setHorizontalLayoutManager(@IdRes int id) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
            setLayoutManager(id, layoutManager);
        }

        public void setGridLayoutManager(@IdRes int id, int spanCount) {
            GridLayoutManager layoutManager = new GridLayoutManager(mContext, spanCount);
            layoutManager.setOrientation(RecyclerView.VERTICAL);
            setLayoutManager(id, layoutManager);
        }

        public void setGridLayoutManager(@IdRes int id, int spanCount, int orientation, boolean reverseLayout) {
            GridLayoutManager layoutManager = new GridLayoutManager(mContext, spanCount, orientation, reverseLayout);
            setLayoutManager(id, layoutManager);
        }

        public void setLayoutManager(@IdRes int id, @Nullable RecyclerView.LayoutManager layoutManager) {
            ((RecyclerView) getView(id)).setLayoutManager(layoutManager);
        }

        public void setAdapter(@IdRes int id, @Nullable RecyclerView.Adapter adapter) {
            ((RecyclerView) getView(id)).setAdapter(adapter);
        }

        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null && v.getId() == this.itemView.getId()) {
                mOnItemLongClickListener.onItemLongClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
                return true;
            } else if (mOnItemChildLongClickListener != null && v.getId() != this.itemView.getId()) {
                mOnItemChildLongClickListener.onItemChildLongClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
                return true;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null && v.getId() == this.itemView.getId()) {
                mOnItemClickListener.onItemClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
            } else if (mOnItemChildClickListener != null && v.getId() != this.itemView.getId()) {
                mOnItemChildClickListener.onItemChildClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
            }
        }
    }
}
