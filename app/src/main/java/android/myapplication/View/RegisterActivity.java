package android.myapplication.View;

import android.content.Intent;
import android.myapplication.R;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText emailEditText, passwordEditText, confirmPasswordEditText, nameEditText, phoneEditText;
    private TextInputLayout textInputLayoutEmail, textInputLayoutPassword, textInputLayoutConfirmPassword, textInputLayoutName, textInputLayoutPhone;
    private Button registerButton;
    private ImageView icon_back;
   // private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo Firebase Auth và Realtime Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Khởi tạo các phần tử UI
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword);
        textInputLayoutName = findViewById(R.id.textInputLayoutName);
        textInputLayoutPhone = findViewById(R.id.textInputLayoutPhone);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        registerButton = findViewById(R.id.registerButton);
        icon_back = findViewById(R.id.icon_back);

        // Thiết lập TextWatchers cho các trường EditText
        setupTextWatchers();

        // Thiết lập sự kiện click cho nút đăng ký
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //setupPasswordVisibilityToggle();
    }

//    private void setupPasswordVisibilityToggle() {
//        passwordEditText.setOnTouchListener((v, event) -> {
//            final int DRAWABLE_RIGHT = 2;
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                    togglePasswordVisibility();
//                    return true;
//                }
//            }
//            return false;
//        });
//
//        confirmPasswordEditText.setOnTouchListener((v, event) -> {
//            final int DRAWABLE_RIGHT = 2;
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                if (event.getRawX() >= (confirmPasswordEditText.getRight() - confirmPasswordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//                    togglePasswordVisibility1();
//                    return true;
//                }
//            }
//            return false;
//        });
//    }
//
//    private void togglePasswordVisibility() {
//        if(isPasswordVisible) {
//            // Ẩn mật khẩu
//            passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_visible, 0);
//            isPasswordVisible = false;
//        } else {
//            // Hiển thị mật khẩu
//            passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//            passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_invisible, 0);
//            isPasswordVisible = true;
//        }
//    }
//
//    private void togglePasswordVisibility1() {
//        if(isPasswordVisible) {
//            // Ẩn mật khẩu
//            confirmPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            confirmPasswordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_visible, 0);
//            isPasswordVisible = false;
//        } else {
//            // Hiển thị mật khẩu
//            confirmPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//            confirmPasswordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_invisible, 0);
//            isPasswordVisible = true;
//        }
//    }

    private void setupTextWatchers() {
        // Email validation
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString().trim();
                if (!Pattern.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+", email)) {
                    textInputLayoutEmail.setError("Vui lòng nhập email đúng định dạng.");
                } else {
                    textInputLayoutEmail.setErrorEnabled(false);
                    checkDuplicateEmail(email);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Password validation
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                if (!isValidPassword(password)) {
                    textInputLayoutPassword.setError("Mật khẩu phải có ký tự đặc biệt, số, chữ hoa và chữ thường.");
                } else {
                    textInputLayoutPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Confirm password validation
        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String confirmPassword = s.toString();
                String password = passwordEditText.getText().toString();
                if (!confirmPassword.equals(password)) {
                    textInputLayoutConfirmPassword.setError("Mật khẩu xác nhận không khớp.");
                } else {
                    textInputLayoutConfirmPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Name validation
        nameEditText.addTextChangedListener(new TextWatcher() {
            private String currentText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                String filteredInput = input.replaceAll("\\d", ""); // Loại bỏ tất cả các số
                if (!input.equals(filteredInput)) {
                    nameEditText.setText(filteredInput);
                    nameEditText.setSelection(filteredInput.length()); // Đặt con trỏ vào cuối văn bản
                }
                currentText = filteredInput;
            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString().trim();
                if (name.length() < 3) {
                    textInputLayoutName.setError("Tên phải có tối thiểu 3 ký tự.");
                } else {
                    textInputLayoutName.setErrorEnabled(false);
                }
            }
        });


        // Phone number validation
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = s.toString().trim();
                if (!Pattern.matches("0[0-9]{8,9}", phone)) {
                    textInputLayoutPhone.setError("Số điện thoại phải có số 0 ở đầu và từ 9-10 số.");
                } else {
                    textInputLayoutPhone.setErrorEnabled(false);
                    checkDuplicatePhone(phone);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private boolean isValidPassword(String password) {
        // Mật khẩu phải có ít nhất 1 ký tự đặc biệt, 1 số, 1 chữ hoa và 1 chữ thường
        return Pattern.matches("(?=.*[!@#$%^&*()-+=])(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z]).{8,}", password);
    }

    private void checkDuplicateEmail(String email) {
        mDatabase.child("users").orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            textInputLayoutEmail.setError("Email đã tồn tại.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "checkDuplicateEmail:onCancelled", databaseError.toException());
                    }
                });
    }

    private void checkDuplicatePhone(String phone) {
        mDatabase.child("users").orderByChild("phone").equalTo(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            textInputLayoutPhone.setError("Số điện thoại đã tồn tại.");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "checkDuplicatePhone:onCancelled", databaseError.toException());
                    }
                });
    }


    private void registerUser() {

        textInputLayoutEmail.setError(null);
        textInputLayoutPassword.setError(null);
        textInputLayoutConfirmPassword.setError(null);
        textInputLayoutName.setError(null);
        textInputLayoutPhone.setError(null);


        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        boolean hasError = false;

        // Kiểm tra thông tin đầu vào
        if (TextUtils.isEmpty(email)) {
            textInputLayoutEmail.setErrorEnabled(true);
            textInputLayoutEmail.setError("Vui lòng nhập email.");
            hasError = true;
            return;
        } else if (!Pattern.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+", email)) {
            textInputLayoutEmail.setError("Vui lòng nhập email đúng định dạng.");
            hasError = true;
            return;
        }

        if (TextUtils.isEmpty(password)) {
            textInputLayoutPassword.setError("Vui lòng nhập mật khẩu.");
            hasError = true;
            return;
        } else if (!isValidPassword(password)) {
            textInputLayoutPassword.setError("Mật khẩu phải có ký tự đặc biệt, số, chữ hoa và chữ thường.");
            hasError = true;
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            textInputLayoutConfirmPassword.setError("Vui lòng xác nhận lại mật khẩu.");
            hasError = true;
            return;
        } else if (!password.equals(confirmPassword)) {
            textInputLayoutConfirmPassword.setError("Mật khẩu xác nhận không khớp.");
            hasError = true;
            return;
        }

        if (TextUtils.isEmpty(name)) {
            textInputLayoutName.setError("Vui lòng nhập tên.");
            hasError = true;
            return;
        } else if (name.length() < 3) {
            textInputLayoutName.setError("Tên phải có tối thiểu 3 ký tự.");
            hasError = true;
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            textInputLayoutPhone.setError("Vui lòng nhập số điện thoại.");
            hasError = true;
            return;
        } else if (!Pattern.matches("0[0-9]{8,9}", phone)) {
            textInputLayoutPhone.setError("Số điện thoại phải có số 0 ở đầu và tối thiểu 9 số.");
            hasError = true;
            return;
        }

        // Tạo tài khoản người dùng với email và mật khẩu
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // Đăng ký thành công
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        saveUserToDatabase(user.getUid(), email, name, phone);
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Đăng ký thất bại
                    Log.e(TAG, "Đăng ký thất bại", e); // Log lỗi để xem chi tiết trong Logcat
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại.", Toast.LENGTH_SHORT).show();
                });

        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveUserToDatabase(String userId, String email, String name, String phone) {
        // Tạo đối tượng người dùng
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("name", name);
        user.put("phone", phone);

        // Lấy thời gian hiện tại và chuyển đổi sang định dạng dd/MM/yyyy
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        user.put("createdAt", currentDate);  // Lưu ngày tham gia dưới dạng chuỗi

        // Lưu người dùng vào Realtime Database
        mDatabase.child("users").child(userId)
                .setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công.", Toast.LENGTH_SHORT).show();
                    // Chuyển hướng đến màn hình đăng nhập hoặc màn hình chính
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại.", Toast.LENGTH_SHORT).show();
                });
    }
}
