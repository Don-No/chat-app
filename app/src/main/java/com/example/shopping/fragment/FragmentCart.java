package com.example.shopping.fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shopping.BasicActivityInterface;
import com.example.shopping.MyNotifications;
import com.example.shopping.R;
import com.example.shopping.activity.NotificationsActivity;
import com.example.shopping.activity.OrderSuccessActivity;
import com.example.shopping.adapter.CartAdapter;
import com.example.shopping.databinding.FragmentCartBinding;
import com.example.shopping.domain.Items;
import com.example.shopping.domain.Payment;
import com.example.shopping.helper.ChangeNumberItemsListener;
import com.example.shopping.helper.ManagmentCart;
import com.example.shopping.helper.SQLiteHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class FragmentCart extends Fragment implements BasicActivityInterface {
    private FirebaseDatabase database;
    private double tax;
    private ManagmentCart managmentCart;
    private SQLiteHelper db;
    private FragmentCartBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        db = new SQLiteHelper(getActivity());
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        initializeListeners();

        managmentCart = new ManagmentCart(getContext());
        caculatorCart();
        initCartList();
        setupSpinners();

        // Vô hiệu hóa nút checkout và đặt nền khi khởi tạo
        binding.btnCheckout.setEnabled(false);
        binding.btnCheckout.setBackgroundResource(R.drawable.button_bg_add);

        return view;
    }

    private void initializeListeners() {
        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputValid()) {
                    handleCheckout();
                } else {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Thêm lắng nghe sự kiện thay đổi nội dung của các EditText để kích hoạt lại nút khi nhập đủ thông tin
        TextWatcher inputWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputFields();
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        };

        binding.phoneNumberEditText.addTextChangedListener(inputWatcher);
        binding.houseStreetEditText.addTextChangedListener(inputWatcher);
    }

    private boolean isInputValid() {
        return !binding.phoneNumberEditText.getText().toString().trim().isEmpty() &&
                !binding.houseStreetEditText.getText().toString().trim().isEmpty();
    }

    private void checkInputFields() {
        if (isInputValid()) {
            binding.btnCheckout.setEnabled(true);
            binding.btnCheckout.setBackgroundResource(R.drawable.button_bg);
        } else {
            binding.btnCheckout.setEnabled(false);
            binding.btnCheckout.setBackgroundResource(R.drawable.button_bg_add);
        }
    }

    private void handleCheckout() {
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            String phoneNumber = binding.phoneNumberEditText.getText().toString();
            String houseStreet = binding.houseStreetEditText.getText().toString();
            String city = binding.spinnerCity.getSelectedItem().toString();
            String district = binding.spinnerDistrict.getSelectedItem().toString();
            String ward = binding.spinnerWard.getSelectedItem().toString();
            String address = houseStreet + ", " + ward + ", " + district + ", " + city;
            String priceTotalString = binding.priceTotal.getText().toString();

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String currentDateAndTime = dateFormat.format(calendar.getTime());

            JSONArray productsArray = getProductsArray();

            JSONObject orderObject = createOrderObject(userName, email, phoneNumber, address, priceTotalString, currentDateAndTime, productsArray);

            saveOrderToFirebase(orderObject);
        } else {
            Toast.makeText(getContext(), "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private JSONArray getProductsArray() {
        JSONArray productsArray = new JSONArray();
        for (Items item : managmentCart.getListCart()) {
            JSONObject productObject = new JSONObject();
            try {
                productObject.put("title", item.getTitle());
                productObject.put("quantity", item.getNumberinCart());
                productsArray.put(productObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return productsArray;
    }

    private JSONObject createOrderObject(String userName, String email, String phoneNumber, String address, String priceTotal, String currentDateAndTime, JSONArray productsArray) {
        JSONObject orderObject = new JSONObject();
        try {
            orderObject.put("userName", userName);
            orderObject.put("email", email);
            orderObject.put("phoneNumber", phoneNumber);
            orderObject.put("address", address);
            orderObject.put("totalPrice", priceTotal);
            orderObject.put("date", currentDateAndTime);
            orderObject.put("products", productsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return orderObject;
    }

    private void saveOrderToFirebase(JSONObject orderObject) {
        database = FirebaseDatabase.getInstance();
        database.getReference("orders")
                .push()
                .setValue(orderObject.toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            onOrderSaveSuccess();
                        } else {
                            onOrderSaveFailure();
                        }
                    }
                });
    }

    private void onOrderSaveSuccess() {
        Intent intent = new Intent(getActivity(), OrderSuccessActivity.class);
        startActivity(intent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                managmentCart.clearCart();
                initCartList();
            }
        }, 1000);
        String currentDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Payment payment = new Payment(Double.parseDouble(binding.priceTotal.getText().toString().replace("đ", "").replace(",", "").trim()), currentDateAndTime);
        db.insertNoti(payment);
        sendNotification();
    }

    private void onOrderSaveFailure() {
        Toast.makeText(getContext(), "Lưu đơn hàng thất bại!", Toast.LENGTH_SHORT).show();
    }

    private void sendNotification() {
        String totalPrice = binding.priceTotal.getText().toString();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.conner_shop);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent resultIntent = new Intent(getActivity(), NotificationsActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getActivity());
        taskStackBuilder.addParentStack(NotificationsActivity.class);
        taskStackBuilder.addNextIntent(resultIntent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(getNotificationId(), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(requireContext(), MyNotifications.CHANNEL_ID)
                .setContentTitle("Đặt hàng thành công")
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine("Tổng tiền: " + totalPrice))
                .setSmallIcon(R.drawable.ic_noti_active)
                .setLargeIcon(bitmap)
                .setSound(uri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(getNotificationId(), notification);
        }
    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }

    private void initCartList() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }
        binding.rcvCart.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.rcvCart.setAdapter(new CartAdapter(getContext(), managmentCart.getListCart(), new ChangeNumberItemsListener() {
            @Override
            public void changed() {
                caculatorCart();
            }
        }));
        caculatorCart();
    }

    private void caculatorCart() {
        double delivery = 30000;

        double itemTotal = managmentCart.getTotalCartItemsPrice();

        double total = itemTotal + delivery;

        binding.priceSubtotal.setText(managmentCart.formatCurrency(itemTotal));
        binding.priceDelivery.setText(managmentCart.formatCurrency(delivery));
        binding.priceTotal.setText(managmentCart.formatCurrency(total));
    }

    @Override
    public FirebaseDatabase getFirebaseDatabase() {
        return database;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = getActivity().getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void setupSpinners() {
        try {
            String json = loadJSONFromAsset("data_location.json");
            JSONObject jsonObj = new JSONObject(json);

            List<String> cities = new ArrayList<>();
            Iterator<String> cityKeys = jsonObj.keys();
            while (cityKeys.hasNext()) {
                cities.add(cityKeys.next());
            }
            ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, cities);
            cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.spinnerCity.setAdapter(cityAdapter);

            binding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        String city = cities.get(position);
                        JSONObject districtsObj = jsonObj.getJSONObject(city);

                        List<String> districts = new ArrayList<>();
                        Iterator<String> districtKeys = districtsObj.keys();
                        while (districtKeys.hasNext()) {
                            districts.add(districtKeys.next());
                        }
                        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, districts);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.spinnerDistrict.setAdapter(districtAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        String selectedCity = (String) binding.spinnerCity.getSelectedItem();
                        String district = (String) parent.getItemAtPosition(position);
                        JSONArray wards = jsonObj.getJSONObject(selectedCity).getJSONArray(district);
                        List<String> wardNames = new ArrayList<>();
                        for (int i = 0; i < wards.length(); i++) {
                            wardNames.add(wards.getString(i));
                        }
                        ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, wardNames);
                        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.spinnerWard.setAdapter(wardAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
