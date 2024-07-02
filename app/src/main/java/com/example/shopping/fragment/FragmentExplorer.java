package com.example.shopping.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.shopping.BasicActivityInterface;
import com.example.shopping.MainActivity;
import com.example.shopping.R;
import com.example.shopping.activity.NotificationsActivity;
import com.example.shopping.adapter.CategoryAdapter;
import com.example.shopping.adapter.ItemAdapter;
import com.example.shopping.adapter.SliderAdapter;
import com.example.shopping.databinding.FragmentExplorerBinding;
import com.example.shopping.domain.Category;
import com.example.shopping.domain.Items;
import com.example.shopping.domain.SliderItems;
import com.example.shopping.helper.SpaceItemDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FragmentExplorer extends Fragment implements BasicActivityInterface {
    private FirebaseDatabase database;
    private EditText edtSearch;
    private ImageView notifications;
    private ProgressBar progressBar, progressBarOffical, progressBarPopular;
    private ViewPager2 viewPagerBanner;
    private boolean isDecorationAdded = false;

    private FragmentExplorerBinding binding;
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExplorerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Lấy đối tượng FirebaseDatabase từ BaseActivityInterface
        BasicActivityInterface baseActivity = (BasicActivityInterface) getActivity();
        if (baseActivity != null) {
            database = baseActivity.getFirebaseDatabase();
        }

        //progressBar = view.findViewById(R.id.progressBar);
        progressBarPopular = view.findViewById(R.id.progressBarPopular);
        //viewPagerBanner = view.findViewById(R.id.viewPagerSlider);
        edtSearch = view.findViewById(R.id.edtSearch);
        notifications = view.findViewById(R.id.iconNotifications);


        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NotificationsActivity.class);
                startActivity(intent);
            }
        });

        binding.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadAllItems();
            }
        });

        setupStyleFilters();
        setupSearchEditText();
        //initBanner();
        //initCategory();
        initItems();

        return view;
    }

    private void loadAllItems() {
        DatabaseReference dbRef = database.getReference("Items");
        progressBarPopular.setVisibility(View.VISIBLE);
        binding.rcvPopular.setVisibility(View.GONE);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Items> allItems = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Items item = dataSnapshot.getValue(Items.class);
                    if (item != null) {
                        allItems.add(item);
                    }
                }
                binding.rcvPopular.setAdapter(new ItemAdapter(getContext(), allItems));
                if (!isDecorationAdded) {
                    binding.rcvPopular.addItemDecoration(new SpaceItemDecoration(dpToPx(12)));
                    isDecorationAdded = true;
                }
                progressBarPopular.setVisibility(View.GONE);
                binding.rcvPopular.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarPopular.setVisibility(View.GONE);
            }
        });
    }
    private void setupStyleFilters() {

        binding.style1.setOnClickListener(v -> filterItemsByStatus("đáng yêu"));
        binding.style2.setOnClickListener(v -> filterItemsByStatus("bí ẩn"));
        binding.style3.setOnClickListener(v -> filterItemsByStatus("vui tươi"));
    }

    private void filterItemsByStatus(String status) {
        DatabaseReference dbRef = database.getReference("Items");
        final ArrayList<Items> items = new ArrayList<>();
        progressBarPopular.setVisibility(View.VISIBLE);
        binding.rcvPopular.setVisibility(View.GONE);
        Query query = dbRef.orderByChild("status").equalTo(status);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Items> filteredItems = new ArrayList<>();
                for (DataSnapshot issue : snapshot.getChildren()) {
                    Items item = issue.getValue(Items.class);
                    if (item != null && item.getStatus().equals(status)) {
                        items.add(item);
                    }
                }
                binding.rcvPopular.setAdapter(new ItemAdapter(getContext(), items));
                if (!isDecorationAdded) {
                    binding.rcvPopular.addItemDecoration(new SpaceItemDecoration(dpToPx(12)));
                    isDecorationAdded = true;
                }
                progressBarPopular.setVisibility(View.GONE);
                binding.rcvPopular.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarPopular.setVisibility(View.GONE);
            }
        });
    }

    private void setupSearchEditText() {
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchItems(edtSearch.getText().toString());
                    hideKeyboard(edtSearch);
                    edtSearch.clearFocus();
                    return true;
                }
                return false;
            }
        });

    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void initBanner() {
        DatabaseReference dbRef = database.getReference("Banner");
        progressBar.setVisibility(View.VISIBLE);
        ArrayList<SliderItems> items = new ArrayList<>();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(SliderItems.class));
                    }
                }
                banners(items);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initItems() {
        DatabaseReference dbRef = database.getReference("Items");
        progressBarPopular.setVisibility(View.VISIBLE);
        ArrayList<Items> items = new ArrayList<>();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(Items.class));
                    }
                    if(!items.isEmpty()) {
                        binding.rcvPopular.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                        binding.rcvPopular.setAdapter(new ItemAdapter(getContext(), items));
                        if (!isDecorationAdded) {
                            binding.rcvPopular.addItemDecoration(new SpaceItemDecoration(dpToPx(12)));
                            isDecorationAdded = true;
                        }
                    }
                    progressBarPopular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
    private void searchItems(String query) {
        DatabaseReference dbRef = database.getReference("Items");
        progressBarPopular.setVisibility(View.VISIBLE);
        binding.rcvPopular.setVisibility(View.GONE);
        final ArrayList<Items> items = new ArrayList<>();
        final String[] keywords = query.toLowerCase().split("\\s+");

        Query searchQuery = dbRef.orderByChild("title");
        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot issue : snapshot.getChildren()) {
                    Items item = issue.getValue(Items.class);
                    if (item != null) {
                        String title = item.getTitle().toLowerCase();
                        boolean containsAllKeywords = true;
                        for (String keyword : keywords) {
                            if (!title.contains(keyword)) {
                                containsAllKeywords = false;
                                break;
                            }
                        }
                        if (containsAllKeywords) {
                            items.add(item);
                        }
                    }
                }
                if (!items.isEmpty()) {
                    binding.rcvPopular.setAdapter(new ItemAdapter(getContext(), items));
                    progressBarPopular.setVisibility(View.GONE);
                    binding.rcvPopular.setVisibility(View.VISIBLE);
                } else {
                    // Hiển thị rcv rỗng nếu không tìm thấy kết quả
                    binding.rcvPopular.setAdapter(new ItemAdapter(getContext(), new ArrayList<>()));
                    if (!isDecorationAdded) {
                        binding.rcvPopular.addItemDecoration(new SpaceItemDecoration(dpToPx(12)));
                        isDecorationAdded = true;
                    }
                    progressBarPopular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarPopular.setVisibility(View.GONE);
            }
        });
    }


    private void banners(ArrayList<SliderItems> items) {
        viewPagerBanner.setAdapter(new SliderAdapter(getContext(), items, viewPagerBanner));
        viewPagerBanner.setClipToPadding(false);
        viewPagerBanner.setClipChildren(false);
        viewPagerBanner.setOffscreenPageLimit(3);
        viewPagerBanner.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        viewPagerBanner.setPageTransformer(compositePageTransformer);
    }

    @Override
    public FirebaseDatabase getFirebaseDatabase() {
        return database;
    }
}
