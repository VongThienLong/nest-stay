package android.myapplication.View;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.myapplication.Controller.OrderAdapter;
import android.myapplication.Model.ProductCategory;
import android.myapplication.R;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private ImageButton cartButton;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private List<ProductCategory> categories = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("categories");

        fetchCategoriesFromFirebase();

    }

    private void fetchCategoriesFromFirebase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String image = categorySnapshot.child("image").getValue(String.class);
                    String name = categorySnapshot.child("name").getValue(String.class);
                    ProductCategory category = new ProductCategory(name, image);
                    categories.add(category);
                }

                // Use the categories list
                for (ProductCategory category : categories) {
                    Log.d(TAG, "Name: " + category.getName() + ", Image: " + category.getImage());
                }

                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                orderAdapter = new OrderAdapter(categories);
                recyclerView.setAdapter(orderAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        cartButton = view.findViewById(R.id.cart_icon);
        cartButton.setOnClickListener(v -> {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(mAuth.getCurrentUser().getUid())
                    .child("cart");

            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                        // Cart is not empty
                        Intent intent = new Intent(getContext(), OrderPaymentActivity.class);
                        startActivity(intent);
                    } else {
                        // Cart is empty
                        Toast.makeText(getContext(), "Chưa có sản phẩm trong giỏ hàng.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                    Toast.makeText(getContext(), "Failed to check cart. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        });


        return view;
    }
}