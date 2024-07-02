package com.example.shopping.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.R;
import com.example.shopping.adapter.WishlistAdapter;
import com.example.shopping.domain.Items;
import com.example.shopping.helper.SQLiteHelper;

import java.util.ArrayList;

public class FragmentWishlist extends Fragment {

    private RecyclerView rcvWishList;
    private ImageView emptyWishlist;
    private WishlistAdapter wishlistAdapter;
    private SQLiteHelper db;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        AppCompatButton btnCheckout = view.findViewById(R.id.btnCheckout);

        // Initialize your SQLiteHelper and RecyclerView here if needed
        // db = new SQLiteHelper(getContext());
        // rcvWishList = view.findViewById(R.id.rcvWishlist);
        // emptyWishlist = view.findViewById(R.id.emptyWishlistTxt);
        // updateUI();
        // wishlistAdapter = new WishlistAdapter(getContext());
        // LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        // rcvWishList.setLayoutManager(linearLayoutManager);
        // ArrayList<Items> list = db.getAllItem();
        // wishlistAdapter.setData(list);
        // rcvWishList.setAdapter(wishlistAdapter);

        setItemClickListener(view, R.id.item_1, R.id.text_1, btnCheckout);
        setItemClickListener(view, R.id.item_2, R.id.text_2, btnCheckout);
        setItemClickListener(view, R.id.item_4, R.id.text_4, btnCheckout);
        setItemClickListener(view, R.id.item_5, R.id.text_5, btnCheckout);
        setItemClickListener(view, R.id.item_6, R.id.text_6, btnCheckout);
        setItemClickListener(view, R.id.item_7, R.id.text_7, btnCheckout);
        setItemClickListener(view, R.id.item_8, R.id.text_8, btnCheckout);
        setItemClickListener(view, R.id.item_9, R.id.text_9, btnCheckout);

        return view;
    }

    private void setItemClickListener(View parent, int itemId, int textId, AppCompatButton checkoutButton) {
        LinearLayout item = parent.findViewById(itemId);
        TextView text = parent.findViewById(textId);

        item.setOnClickListener(v -> {
            boolean isClicked = !text.getTag().equals("clicked");
            if (isClicked) {
                text.setBackgroundResource(R.drawable.rounded_bottom_selected);
                text.setTextColor(Color.WHITE);
                text.setTag("clicked");
                checkoutButton.setBackgroundResource(R.drawable.button_bg_add_selected);
            } else {
                text.setBackgroundResource(R.drawable.rounded_bottom_img);
                text.setTextColor(Color.BLACK);
                text.setTag("unclicked");
                if (allItemsUnclicked(parent)) {
                    checkoutButton.setBackgroundResource(R.drawable.button_bg_add);
                }
            }
        });

        text.setTag("unclicked");
    }

    private boolean allItemsUnclicked(View parent) {
        // Thêm tất cả các id của TextView tương ứng với các item
        int[] textIds = {R.id.text_1, R.id.text_2, R.id.text_4, R.id.text_5, R.id.text_6, R.id.text_7, R.id.text_8, R.id.text_9};
        for (int id : textIds) {
            TextView textView = parent.findViewById(id);
            if (textView.getTag().equals("clicked")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        // updateUI();
        // ArrayList<Items> list = db.getAllItem();
        // wishlistAdapter.setData(list);
        // wishlistAdapter.notifyDataSetChanged();
    }

    private void updateUI() {
        ArrayList<Items> list = db.getAllItem();
        if (list.isEmpty()) {
            emptyWishlist.setVisibility(View.VISIBLE);
            rcvWishList.setVisibility(View.GONE);
        } else {
            emptyWishlist.setVisibility(View.GONE);
            rcvWishList.setVisibility(View.VISIBLE);
        }
    }
}
