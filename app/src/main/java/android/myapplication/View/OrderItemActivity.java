package android.myapplication.View;

import android.content.Intent;
import android.myapplication.Controller.OrderItemAdapter;
import android.myapplication.Model.Product;
import android.myapplication.R;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderItemActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Product> productList;
    private String categoryName;
    private ImageButton cartButton;
    private ImageView backButton;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_item);
        categoryName = getIntent().getStringExtra("productCategory");
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        backButton = findViewById(R.id.icon_back);
        cartButton = findViewById(R.id.cart_icon);
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
                        Intent intent = new Intent(OrderItemActivity.this, OrderPaymentActivity.class);
                        startActivity(intent);
                    } else {
                        // Cart is empty
                        Toast.makeText(getApplicationContext(), "Chưa có sản phẩm trong giỏ hàng.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                    Toast.makeText(getApplicationContext(), "Failed to check cart. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        backButton.setOnClickListener(v -> finish());

        // Set the category name
        TextView categoryTitle = findViewById(R.id.categoryTitle);
        categoryTitle.setText(categoryName);


        // Get items from firebase database where name is equal to categoryName
        mDatabase.child("categories").orderByChild("name").equalTo(categoryName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList = new ArrayList<>();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    productSnapshot.child("items").getValue();
                    for (DataSnapshot itemSnapshot : productSnapshot.child("items").getChildren()) {
                        String name = itemSnapshot.child("name").getValue(String.class);
                        String image = itemSnapshot.child("image").getValue(String.class);
                        int price = itemSnapshot.child("price").getValue(Integer.class);
                        String description = itemSnapshot.child("description").getValue(String.class);
                        Product product = new Product(name, image, price, description);
                        productList.add(product);
                    }
                }

                // Use the productList
                for (Product product : productList) {
                    Log.d("OrderItemActivity", "Name: " + product.getName() + ", Image: " + product.getImage());
                }

                // Initialize the recycler view
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(OrderItemActivity.this));
                recyclerView.addItemDecoration(new DividerItemDecoration(OrderItemActivity.this, DividerItemDecoration.VERTICAL));
                recyclerView.setAdapter(new OrderItemAdapter(productList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("OrderItemActivity", "Failed to read value.", error.toException());
            }
        });




        // Initialize the recycler view
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new OrderItemAdapter(productList));

    }



}