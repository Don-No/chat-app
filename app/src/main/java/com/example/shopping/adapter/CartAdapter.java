package com.example.shopping.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.shopping.R;
import com.example.shopping.domain.Items;
import com.example.shopping.helper.ChangeNumberItemsListener;
import com.example.shopping.helper.ManagmentCart;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private ArrayList<Items> listItemSelected;
    private Context context;
    ChangeNumberItemsListener changeNumberItemsListener;
    private ManagmentCart managmentCart;

    public CartAdapter(Context context, ArrayList<Items> listItemSelected, ChangeNumberItemsListener changeNumberItemsListener) {
        this.context = context;
        this.listItemSelected = listItemSelected;
        this.changeNumberItemsListener = changeNumberItemsListener;
        managmentCart = new ManagmentCart(context);
    }

    private String formatCurrency(double amount) {
        return String.format("%,.0fđ", amount);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String priceString = listItemSelected.get(position).getPrice().replace("đ", "").replace(".", "");
        double price = Double.parseDouble(priceString);
        holder.titleTxt.setText(listItemSelected.get(position).getTitle());
        int numberInCart = listItemSelected.get(position).getNumberinCart();
        double totalItemPrice = numberInCart * price;
        holder.totalEachItem.setText(formatCurrency(totalItemPrice));
        holder.numberItemTxt.setText(String.valueOf(listItemSelected.get(position).getNumberinCart()));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transform(new CenterCrop());

        Glide.with(holder.itemView.getContext())
                .load(listItemSelected.get(position).getPicUrl().get(0))
                .apply(requestOptions)
                .into(holder.carPic);

        holder.plusCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                managmentCart.plusItem(listItemSelected, position, new ChangeNumberItemsListener() {
                    @Override
                    public void changed() {
                        notifyDataSetChanged();
                        changeNumberItemsListener.changed();
                    }
                });
            }
        });

        holder.minusCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                managmentCart.minusItem(listItemSelected, position, new ChangeNumberItemsListener() {
                    @Override
                    public void changed() {
                        notifyDataSetChanged();
                        changeNumberItemsListener.changed();
                    }
                });
            }
        });

        holder.icDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                managmentCart.removeItem(listItemSelected, position, new ChangeNumberItemsListener() {
                    @Override
                    public void changed() {
                        notifyDataSetChanged();
                        changeNumberItemsListener.changed();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView carPic, icDelete;
        private TextView titleTxt, totalEachItem, plusCartBtn, minusCartBtn, numberItemTxt;
        public ViewHolder(@NonNull View view) {
            super(view);
            carPic = view.findViewById(R.id.cart_pic);
            titleTxt = view.findViewById(R.id.titleTxt);
            totalEachItem = view.findViewById(R.id.totalEachItem);
            plusCartBtn = view.findViewById(R.id.plusCartBtn);
            minusCartBtn = view.findViewById(R.id.minusCartBtn);
            numberItemTxt = view.findViewById(R.id.numberItemTxt);
            icDelete = view.findViewById(R.id.ic_delete);
        }
    }
}
