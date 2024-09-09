package android.myapplication.View;
import android.myapplication.R;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ItemDescriptionActivity extends AppCompatActivity {

    private Button buttonAddToCart;
    private ImageView productImage, iconBack, iconFavorite, buttonDecrease, buttonIncrease;
    private TextView productName, productDescription, productPrice, textViewQuantity;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_description);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        initView();
        setupProductDetails();
        setupFavoriteIcon();
        setupCartQuantity();

        iconBack.setOnClickListener(v -> finish());

        buttonIncrease.setOnClickListener(v -> updateQuantity(1));
        buttonDecrease.setOnClickListener(v -> updateQuantity(-1));

        iconFavorite.setOnClickListener(v -> toggleFavorite());

        buttonAddToCart.setOnClickListener(v -> addToCart());
    }

    private void initView() {
        buttonAddToCart = findViewById(R.id.buttonAddToCart);
        buttonIncrease = findViewById(R.id.buttonIncrease);
        buttonDecrease = findViewById(R.id.buttonDecrease);
        textViewQuantity = findViewById(R.id.textViewQuantity);
        iconBack = findViewById(R.id.icon_back);
        productImage = findViewById(R.id.itemImage);
        productName = findViewById(R.id.itemName);
        productDescription = findViewById(R.id.itemDescription);
        productPrice = findViewById(R.id.itemPrice);
        iconFavorite = findViewById(R.id.icon_favorite);
    }

    private void setupProductDetails() {
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        String price = getIntent().getStringExtra("price");
        String image = getIntent().getStringExtra("image");
        String quantity = getIntent().getStringExtra("quantity");

        productName.setText(name);
        productDescription.setText(description);
        productPrice.setText(price + "đ");

        textViewQuantity.setText(quantity != null ? quantity : "1");

        if (image != null && !image.isEmpty()) {
            Picasso.get()
                    .load(image)
                    .error(R.drawable.placeholder)
                    .into(productImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Image loaded successfully
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
        } else {
            productImage.setImageResource(R.drawable.placeholder);
        }
    }

    private void setupFavoriteIcon() {
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            String itemName = productName.getText().toString();

            mDatabase.child("users").child(userId).child("favorites").child(itemName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            updateFavoriteIcon(dataSnapshot.exists());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(ItemDescriptionActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setupCartQuantity() {
        if (mAuth != null && mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            String itemName = productName.getText().toString();

            mDatabase.child("users").child(userId).child("cart").child(itemName)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String existingQuantityStr = dataSnapshot.child("quantity").getValue(String.class);
                                textViewQuantity.setText(existingQuantityStr);
                                buttonDecrease.setEnabled(true);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(ItemDescriptionActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateQuantity(int delta) {
        int quantity = Integer.parseInt(textViewQuantity.getText().toString()) + delta;
        textViewQuantity.setText(String.valueOf(quantity));
        buttonDecrease.setEnabled(quantity > 1);
    }

    private void toggleFavorite() {
        boolean isFavorite = "favorite".equals(iconFavorite.getTag());
        String name = productName.getText().toString();
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference itemRef = mDatabase.child("users").child(userId).child("favorites").child(name);

        if (isFavorite) {
            itemRef.removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ItemDescriptionActivity.this, "Đã xóa món ăn khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                            updateFavoriteIcon(false);
                        } else {
                            Toast.makeText(ItemDescriptionActivity.this, "Xóa món ăn khỏi danh sách yêu thích thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Map<String, Object> itemDetails = getItemDetails();
            itemRef.setValue(itemDetails)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ItemDescriptionActivity.this, "Thêm món ăn vào danh sách yêu thích thành công", Toast.LENGTH_SHORT).show();
                            updateFavoriteIcon(true);
                        } else {
                            Toast.makeText(ItemDescriptionActivity.this, "Thêm món ăn vào danh sách yêu thích thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        iconFavorite.setImageResource(isFavorite ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);
        iconFavorite.setTag(isFavorite ? "favorite" : "not_favorite");
    }

    private void addToCart() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String itemName = productName.getText().toString();
            DatabaseReference itemRef = mDatabase.child("users").child(userId).child("cart").child(itemName);
            int quantity = Integer.parseInt(textViewQuantity.getText().toString());

            itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (quantity == 0) {
                        itemRef.removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ItemDescriptionActivity.this, "Đã xóa món ăn", Toast.LENGTH_SHORT).show();
                                buttonDecrease.setEnabled(false);
                                finish();
                            } else {
                                Toast.makeText(ItemDescriptionActivity.this, "Bỏ món thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Map<String, Object> itemDetails = getItemDetails();
                        itemDetails.put("quantity", String.valueOf(quantity));

                        itemRef.setValue(itemDetails).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ItemDescriptionActivity.this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
                                buttonDecrease.setEnabled(true);
                                finish();
                            } else {
                                Toast.makeText(ItemDescriptionActivity.this, "Thêm món ăn thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ItemDescriptionActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Map<String, Object> getItemDetails() {
        Map<String, Object> itemDetails = new HashMap<>();
        itemDetails.put("name", productName.getText().toString());
        itemDetails.put("description", productDescription.getText().toString());
        itemDetails.put("price", Integer.parseInt(productPrice.getText().toString().replace("đ", "")));
        itemDetails.put("image", getIntent().getStringExtra("image"));
        return itemDetails;
    }

}
