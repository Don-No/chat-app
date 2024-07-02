package com.example.shopping.helper;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecorationOrders extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemDecorationOrders(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = space;
        outRect.bottom = space;
    }
}
