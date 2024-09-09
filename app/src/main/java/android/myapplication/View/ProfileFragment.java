package android.myapplication.View;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.myapplication.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private TextView userNameTextView;
    private ImageView profileImage;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements
        userNameTextView = view.findViewById(R.id.profile_name);
        profileImage = view.findViewById(R.id.profile_image);
        LinearLayout linearLayoutAddress = view.findViewById(R.id.linearLayoutAddress);
        LinearLayout linearLayoutHistory = view.findViewById(R.id.LinerLayouthistory);
        LinearLayout linearLayoutFavorite = view.findViewById(R.id.linearLayoutFavorite);
        View logoutButton = view.findViewById(R.id.logout);
        View editProfileButton = view.findViewById(R.id.editprofile);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        View deleteAccountButton = view.findViewById(R.id.deleteAccountButton);

        // Set click listeners
        linearLayoutHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HistoryActivity.class);
            startActivity(intent);
        });

        linearLayoutAddress.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddressProfileActivity.class);
            startActivity(intent);
        });

        linearLayoutFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FavoriteItemActivity.class);
            startActivity(intent);
        });

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(this::logout);
        deleteAccountButton.setOnClickListener(this::deleteAccount);

        // Fetch and display user data
        fetchAndDisplayUserData();

        return view;
    }

    private void fetchAndDisplayUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && getActivity() != null) { // Ensure Fragment is attached
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String profileImageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                        // Display user data
                        userNameTextView.setText(name);

                        // Display profile image
                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(ProfileFragment.this)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.meme)
                                    .into(profileImage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }

    private void logout(View view) {
        FirebaseAuth.getInstance().signOut();

        // Clear stored login information
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("login_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("remember_me", false);
        editor.remove("email");
        editor.remove("password");
        editor.apply();

        // Navigate to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        requireActivity().finish();
    }

    public void deleteAccount(View view) {
        // Tạo LayoutInflater từ context hiện tại
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        // Gán layout tùy chỉnh vào dialogView
        View dialogView = inflater.inflate(R.layout.dialog_confirm_delete, null);
        // Lấy tham chiếu đến TextInputEditText từ dialogView
        TextInputEditText input = dialogView.findViewById(R.id.etPassword);

        // Tạo và hiển thị hộp thoại xác nhận xóa tài khoản
        new AlertDialog.Builder(requireContext())
                .setTitle("Bạn chắc chắn muốn xóa tài khoản?") // Đặt tiêu đề cho hộp thoại
                .setView(dialogView) // Gán layout tùy chỉnh vào hộp thoại
                .setPositiveButton("Có", (dialog, which) -> {
                    // Lấy mật khẩu người dùng nhập vào
                    String password = input.getText().toString();
                    if (!password.isEmpty()) {
                        // Lấy người dùng hiện tại từ FirebaseAuth
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Tạo chứng thực người dùng từ email và mật khẩu
                            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                            // Tái xác thực người dùng
                            user.reauthenticate(credential).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Xóa tài khoản từ Firebase Authentication
                                    user.delete().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            // Xóa tài khoản từ Firebase Database nếu cần
                                            mDatabase.child("users").child(user.getUid()).removeValue().addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    Toast.makeText(requireContext(), "Xóa tài khoản thành công.", Toast.LENGTH_SHORT).show();
                                                    // Điều hướng người dùng đến màn hình đăng nhập
                                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    requireActivity().finish();
                                                } else {
                                                    Toast.makeText(requireContext(), "Lỗi khi xóa tài khoản từ Database.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(requireContext(), "Lỗi khi xóa tài khoản từ Authentication.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(requireContext(), "Mật khẩu không khớp.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(requireContext(), "Vui lòng nhập mật khẩu.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Không", null) // Nút "Không" không có hành động gì
                .show(); // Hiển thị hộp thoại
    }

}
