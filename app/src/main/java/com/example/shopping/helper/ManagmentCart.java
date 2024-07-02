package com.example.shopping.helper;

import android.content.Context;
import android.widget.Toast;

import com.example.shopping.domain.Items;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ManagmentCart {

    private Context context;
    private TinyDB tinyDB;

    public ManagmentCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    public void insertItem(Items item) {
        ArrayList<Items> listfood = getListCart();
        boolean existAlready = false;
        int n = 0;
        for (int y = 0; y < listfood.size(); y++) {
            if (listfood.get(y).getTitle().equals(item.getTitle())) {
                existAlready = true;
                n = y;
                break;
            }
        }
        if (existAlready) {
            // Cập nhật số lượng sản phẩm
            int currentQuantity = listfood.get(n).getNumberinCart();
            listfood.get(n).setNumberinCart(currentQuantity + item.getNumberinCart());
        } else {
            listfood.add(item);
        }
        tinyDB.putListObject("CartList", listfood);
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<Items> getListCart() {
        return tinyDB.getListObject("CartList");
    }

    public ArrayList<Items> getListWishlist() {
        return tinyDB.getListObject("Wishlist");
    }

    public void removeItem(ArrayList<Items> listfood, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        listfood.remove(position);
        tinyDB.putListObject("CartList", listfood);
        changeNumberItemsListener.changed();
    }

    public void minusItem(ArrayList<Items> listfood, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        if (listfood.get(position).getNumberinCart() == 1) {
            listfood.remove(position);
        } else {
            listfood.get(position).setNumberinCart(listfood.get(position).getNumberinCart() - 1);
        }
        tinyDB.putListObject("CartList", listfood);
        changeNumberItemsListener.changed();
    }

    public void plusItem(ArrayList<Items> listfood, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        listfood.get(position).setNumberinCart(listfood.get(position).getNumberinCart() + 1);
        tinyDB.putListObject("CartList", listfood);
        changeNumberItemsListener.changed();
    }

    public Double getTotalFee() {
        ArrayList<Items> listfood2 = getListCart();
        double fee = 0;
        for (int i = 0; i < listfood2.size(); i++) {
            //fee = fee + (listfood2.get(i).getPrice() * listfood2.get(i).getNumberinCart());
        }
        return fee;
    }

    public double getTotalCartItemsPrice() {
        ArrayList<Items> listfood = getListCart();
        double total = 0;
        for (Items item : listfood) {
            String priceString = item.getPrice().replace("đ", "").replace(".", "");
            double price = Double.parseDouble(priceString);
            total += item.getNumberinCart() * price;
        }
        return total;
    }

    public String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + "đ";
    }

    public void clearCart() {
        tinyDB.putListObject("CartList", new ArrayList<Items>());
    }
}
