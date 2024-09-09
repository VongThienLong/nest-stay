package android.myapplication.View;

import android.content.Intent;
import android.myapplication.Model.AddressProfile;
import android.myapplication.R;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddressActivity extends AppCompatActivity {
    //init firebase database
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    EditText etName, etPhone, etAddress, etNote;
    ImageView iconBack;
    Button btnSubmit;
    String profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etNote = findViewById(R.id.etNotes);
        btnSubmit = findViewById(R.id.btnSubmit);
        iconBack = findViewById(R.id.icon_back);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        if (intent != null) {
            profileId = intent.getStringExtra("PROFILE_ID");
        }

        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String phone = dataSnapshot.child("phone").getValue(String.class);

                        // Hiển thị thông tin người dùng
                        etName.setText(name);
                        etPhone.setText(phone);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý lỗi đọc dữ liệu
                }
            });
        }


        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    etName.setError("Vui lòng nhập tên");
                } else if (!s.toString().matches("^[a-zA-Z\\s]+$")) {
                    etName.setError("Tên không hợp lệ");
                }
            }
        });

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    etPhone.setError("Vui lòng nhập số điện thoại");
                } else if (!s.toString().matches("^0[0-9]{9}$")) {
                    etPhone.setError("Số điện thoại không hợp lệ");
                }
            }
        });

        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    etAddress.setError("Vui lòng nhập địa chỉ");
                }
            }
        });

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String address = etAddress.getText().toString();
            String note = etNote.getText().toString();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                if (name.isEmpty()) {
                    etName.setError("Vui lòng nhập tên");
                }
                if (phone.isEmpty()) {
                    etPhone.setError("Vui lòng nhập số điện thoại");
                }
                if (address.isEmpty()) {
                    etAddress.setError("Vui lòng nhập địa chỉ");
                }
                // Exit the method if any field is empty
                return;
            }

            AddressProfile addressProfile = new AddressProfile(profileId, name, address, phone, note);

            if (profileId != null && !profileId.isEmpty()) {
                // Update existing profile
                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("addressProfiles").child(profileId).setValue(addressProfile)
                        .addOnSuccessListener(aVoid -> Toast.makeText(AddressActivity.this, "Hồ sơ đã được cập nhật", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(AddressActivity.this, "Cập nhật hồ sơ thất bại", Toast.LENGTH_SHORT).show());
            } else {
                // Create new profile
                String newId = mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("addressProfiles").push().getKey();
                addressProfile.setId(newId); // Set the new ID to the profile
                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("addressProfiles").child(newId).setValue(addressProfile)
                        .addOnSuccessListener(aVoid -> Toast.makeText(AddressActivity.this, "Đã thêm hồ sơ", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(AddressActivity.this, "Thêm hồ sơ thất bại", Toast.LENGTH_SHORT).show());
            }

            finish();
        });

        iconBack.setOnClickListener(v -> {
            finish();
        });

        // Retrieve the profile ID from the intent
        String profileId = getIntent().getStringExtra("PROFILE_ID");

        // Check if the profile ID is not null
        if (profileId != null && !profileId.isEmpty()) {
            // Use the profile ID to fetch the address profile from Firebase
            DatabaseReference profileRef = mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("addressProfiles").child(profileId);
            profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Populate the TextViews with the fetched data
                        AddressProfile addressProfile = dataSnapshot.getValue(AddressProfile.class);
                        if (addressProfile != null) {
                            etName.setText(addressProfile.getName());
                            etPhone.setText(addressProfile.getPhone());
                            etAddress.setText(addressProfile.getAddress());
                            etNote.setText(addressProfile.getNotes());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(AddressActivity.this, "Khởi tạo thất bại.", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

}
