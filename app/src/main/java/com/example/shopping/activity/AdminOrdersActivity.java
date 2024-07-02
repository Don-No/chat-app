package com.example.shopping.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.shopping.adapter.OrdersAdapter;
import com.example.shopping.databinding.ActivityAdminOrdersBinding;
import com.example.shopping.databinding.FragmentProfileBinding;
import com.example.shopping.domain.Order;
import com.example.shopping.helper.SpaceItemDecorationOrders;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminOrdersActivity extends AppCompatActivity {
    private ActivityAdminOrdersBinding binding;
    private OrdersAdapter ordersAdapter;
    private List<Order> orderList;
    private Gson gson;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("815676669470-3740dc23k6lv9rhcvd0l0q7t4e97kkss.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                mGoogleSignInClient.signOut();
                Intent intent = new Intent(AdminOrdersActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        gson = new Gson();
        orderList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(this, orderList);
        binding.recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewOrders.setAdapter(ordersAdapter);
        binding.recyclerViewOrders.addItemDecoration(new SpaceItemDecorationOrders(30)); // Set 10dp space

        fetchOrders();
    }

    private void fetchOrders() {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String json = snapshot.getValue(String.class);
                    Order order = gson.fromJson(json, Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                // Sort orders by date descending after fetching
                Collections.sort(orderList, (o1, o2) -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        return sdf.parse(o2.getDate()).compareTo(sdf.parse(o1.getDate()));
                    } catch (Exception e) {
                        throw new IllegalArgumentException(e);
                    }
                });
                ordersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
}
