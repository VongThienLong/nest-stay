package android.myapplication.View;

import android.content.Intent;
import android.myapplication.Controller.OrderItemAdapter;
import android.myapplication.Model.Product;
import android.myapplication.R;
import android.os.Bundle;
import android.widget.ImageView;
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

public class FavoriteItemActivity extends AppCompatActivity {
    private List<Product> productList;
    private RecyclerView recyclerView;
    private ImageView backButton, cartButton;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private OrderItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_item);

        productList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.icon_back);
        cartButton = findViewById(R.id.cart_icon);

        backButton.setOnClickListener(v -> finish());

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
                        Intent intent = new Intent(FavoriteItemActivity.this, OrderPaymentActivity.class);
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

        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new OrderItemAdapter(productList);
        recyclerView.setAdapter(adapter);

        // Listen for changes in the list of favorite items
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("favorites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        productList.clear();
                        for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                            Product product = productSnapshot.getValue(Product.class);
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(FavoriteItemActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
