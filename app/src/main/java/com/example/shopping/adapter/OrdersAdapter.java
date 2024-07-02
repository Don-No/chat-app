package com.example.shopping.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopping.R;
import com.example.shopping.domain.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orderList;

    public OrdersAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.orderUserName.setText(order.getUserName());
        holder.orderEmail.setText(order.getEmail());
        holder.orderPhoneNumber.setText(order.getPhoneNumber());
        holder.orderDate.setText(order.getDate());
        holder.orderTotalPrice.setText(order.getTotalPrice());
        holder.orderAddress.setText(order.getAddress());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date orderDate = sdf.parse(order.getDate());
            Date currentDate = new Date();
            long diff = currentDate.getTime() - orderDate.getTime();
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            if (hours <= 24) {
                holder.iconNew.setVisibility(View.VISIBLE);
            } else {
                holder.iconNew.setVisibility(View.GONE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            holder.iconNew.setVisibility(View.GONE);
        }

        StringBuilder productsBuilder = new StringBuilder();
        for (Order.Product product : order.getProducts()) {
            productsBuilder.append(product.getTitle())
                    .append(" (x")
                    .append(product.getQuantity())
                    .append(")\n");
        }
        holder.orderProducts.setText(productsBuilder.toString());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderUserName, orderEmail, orderPhoneNumber, orderDate, orderTotalPrice, orderAddress, orderProducts;
        ImageView iconNew;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderUserName = itemView.findViewById(R.id.orderUserName);
            orderEmail = itemView.findViewById(R.id.orderEmail);
            orderPhoneNumber = itemView.findViewById(R.id.orderPhoneNumber);
            orderDate = itemView.findViewById(R.id.orderDate);
            orderTotalPrice = itemView.findViewById(R.id.orderTotalPrice);
            orderAddress = itemView.findViewById(R.id.orderAddress);
            orderProducts = itemView.findViewById(R.id.orderProducts);
            iconNew = itemView.findViewById(R.id.iconNew);
        }
    }
}
