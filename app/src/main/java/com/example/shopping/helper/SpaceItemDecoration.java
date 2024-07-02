package com.example.shopping.helper;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int spanCount = ((GridLayoutManager) parent.getLayoutManager()).getSpanCount();

        if (spanCount == 2) {
            int column = position % spanCount; // Tính toán cột hiện tại (0 hoặc 1)
            outRect.left = column == 0 ? space * 3 : (space / 2) + 20; // Đặt khoảng trống bên trái cho cột 0 và cột 1
            outRect.right = column == 0 ? (space / 2) + 20 : space; // Đặt khoảng trống bên phải cho cột 0 và cột 1
            outRect.top = space;  // Đặt khoảng trống trên
            outRect.bottom = space;  // Đặt khoảng trống dưới
        }
    }
}
