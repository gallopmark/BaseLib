package com.holike.baseutils;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import pony.xcode.base.ViewPagerFragment;
import pony.xcode.recycler.CommonAdapter;
import pony.xcode.swipe.OnSwipeItemTouchListener;

/**
 * Created by pony on 2019/11/15.
 * Version v3.0 app报表
 */
public class MainFragment extends ViewPagerFragment {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_main;
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
        toolbar.inflateMenu(R.menu.menu_main);
        RecyclerView rv = mContentView.findViewById(R.id.rv);
        rv.addOnItemTouchListener(new OnSwipeItemTouchListener(mContext));
        List<ItemBean> items = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            items.add(i, new ItemBean("item " + (i + 1)));
        }
        rv.setAdapter(new CommonAdapter<ItemBean>(mContext, items) {

            @Override
            protected int getItemResourceId(int viewType) {
                return R.layout.item_rv;
            }

            @Override
            protected void bindViewHolder(RecyclerHolder holder, ItemBean item, final int position) {
                holder.setText(R.id.tv_text, item.getText());
                holder.setOnClickListener(R.id.tv_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Snackbar.make(view, "删除item" + position, Snackbar.LENGTH_LONG).show();
                        mDataList.remove(position);
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    protected void startLoad() {
        Log.e("main", "load start...");
    }
}
