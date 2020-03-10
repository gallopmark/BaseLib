package pony.xcode.recycler;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public abstract class CommonAdapter<T> extends BaseRecyclerAdapter {

    protected List<T> mDataList;

    public CommonAdapter(Context context, List<T> dataList) {
        super(context);
        this.mDataList = dataList;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerHolder(mLayoutInflater.inflate(getItemResourceId(viewType), parent, false));
    }

    @LayoutRes
    protected abstract int getItemResourceId(int viewType);

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        bindViewHolder(holder, mDataList.get(position), position);
    }

    protected abstract void bindViewHolder(RecyclerHolder holder, T t, int position);

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Nullable
    public T getItem(int position) {
        if (mDataList == null || mDataList.isEmpty()
                || position < 0 || position >= mDataList.size())
            return null;
        return mDataList.get(position);
    }
}
