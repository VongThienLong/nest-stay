package android.myapplication.View;

import android.content.Intent;
import android.myapplication.Controller.AddressProfileAdapter;
import android.myapplication.Model.AddressProfile;
import android.myapplication.R;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class AddressProfileActivity extends AppCompatActivity {
    String name, phone, address, note;
    private static final int ADDRESS_REQUEST_CODE = 1;
    RecyclerView recyclerView;
    AddressProfileAdapter addressProfileAdapter;
    ImageButton addAddressProfile;
    ImageView iconBack;
    TextView tvEmpty;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        addAddressProfile = findViewById(R.id.btnAddAddress);
        iconBack = findViewById(R.id.icon_back);
        tvEmpty = findViewById(R.id.tvEmpty);

        addAddressProfile.setOnClickListener(v -> {
            Intent intent = new Intent(AddressProfileActivity.this, AddressActivity.class);
            startActivityForResult(intent, 1);
        });

        iconBack.setOnClickListener(v -> {
            finish();
        });

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("addressProfiles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<AddressProfile> addressProfiles = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AddressProfile addressProfile = snapshot.getValue(AddressProfile.class);
                    addressProfiles.add(addressProfile);
                }
                if (addressProfiles.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Chưa có hồ sơ địa chỉ nào. Bấm vào nút để thêm mới.");
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    addressProfileAdapter = new AddressProfileAdapter(addressProfiles);
                    recyclerView.setLayoutManager(new LinearLayoutManager(AddressProfileActivity.this));
                    recyclerView.setAdapter(addressProfileAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("AddressProfileActivity", "Failed to read address profiles", databaseError.toException());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDRESS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Extract the data from the intent
            name = data.getStringExtra("name");
            phone = data.getStringExtra("phone");
            address = data.getStringExtra("address");
            note = data.getStringExtra("note");

            // Refresh the recycler view
            addressProfileAdapter.notifyDataSetChanged();
        }
    }
}