package android.myapplication.View;

import android.content.Intent;
import android.myapplication.R;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReservationFragment extends Fragment {

    private CalendarView calendarView;
    private TimePicker timePicker;
    private TextView number1, number2, number3, number4, number5, number6, number7, number8, number9, number10;
    private EditText etName, etPhone, etAddress, etEmail, etNotes;
    private Button btnConfirm;
    private int numberOfPeople;
    private long selectedDate;

    private boolean isOperatingHoursWarningShown = false;


    public ReservationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        timePicker = view.findViewById(R.id.timePicker);
        number1 = view.findViewById(R.id.number1);
        number2 = view.findViewById(R.id.number2);
        number3 = view.findViewById(R.id.number3);
        number4 = view.findViewById(R.id.number4);
        number5 = view.findViewById(R.id.number5);
        number6 = view.findViewById(R.id.number6);
        number7 = view.findViewById(R.id.number7);
        number8 = view.findViewById(R.id.number8);
        number9 = view.findViewById(R.id.number9);
        number10 = view.findViewById(R.id.number10);
        etName = view.findViewById(R.id.etName);
        etPhone = view.findViewById(R.id.etPhone);
        etAddress = view.findViewById(R.id.etAddress);
        etEmail = view.findViewById(R.id.etEmail);
        etNotes = view.findViewById(R.id.etNotes);
        btnConfirm = view.findViewById(R.id.btnConfirm);

        // Khởi tạo các phần tử UI cho cập nhật từ firebase xuống ứng dụng
        EditText etName = view.findViewById(R.id.etName);
        EditText etPhone = view.findViewById(R.id.etPhone);
        EditText etEmail = view.findViewById(R.id.etEmail);

        // Initialize your TextInputLayouts
        TextInputLayout textInputLayoutName = view.findViewById(R.id.textInputLayoutName);
        TextInputLayout textInputLayoutPhone = view.findViewById(R.id.textInputLayoutPhone);
        TextInputLayout textInputLayoutAddress = view.findViewById(R.id.textInputLayoutAddress);
        TextInputLayout textInputLayoutEmail = view.findViewById(R.id.textInputLayoutEmail);


        // Lấy thông tin người dùng từ Firebase Realtime Database để cập nhật name
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String phone = dataSnapshot.child("phone").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String address = dataSnapshot.child("address").getValue(String.class);

                        // Hiển thị thông tin người dùng
                        etName.setText(name);
                        etAddress.setText(address);
                        etPhone.setText(phone);
                        etEmail.setText(email);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý lỗi đọc dữ liệu
                }
            });
        }

        // Add TextWatcher để xử lý khi người dùng nhập kí tự trong edt và biến mất error
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần làm gì trước khi text thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra nếu tên trống
                if (s.length() == 0) {
                    textInputLayoutName.setErrorEnabled(true);
                    textInputLayoutName.setError("Vui lòng nhập tên.");
                } else {
                    textInputLayoutName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần làm gì sau khi text thay đổi
            }
        });


        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần làm gì trước khi text thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra nếu số điện thoại không hợp lệ
                if (s.length() < 9 || s.length() > 10 || !s.toString().startsWith("0")) {
                    textInputLayoutPhone.setError("Số điện thoại phải có số 0 ở đầu và tối thiểu 9 số.");
                } else {
                    textInputLayoutPhone.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần làm gì sau khi text thay đổi
            }
        });

        etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần làm gì trước khi text thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra nếu địa chỉ trống
                if (s.length() == 0) {
                    textInputLayoutAddress.setError("Vui lòng nhập địa chỉ.");
                } else {
                    textInputLayoutAddress.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần làm gì sau khi text thay đổi
            }
        });

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần làm gì trước khi text thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra nếu email không hợp lệ
                if (s.length() > 0 && !isValidEmail(s.toString())) {
                    textInputLayoutEmail.setError("Vui lòng nhập email đúng định dạng.");
                } else {
                    textInputLayoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần làm gì sau khi text thay đổi
            }
        });


        calendarView.setMinDate(System.currentTimeMillis() - 1000);

        // Đặt selectedDate mặc định là ngày hiện tại
        Calendar today = Calendar.getInstance();
        selectedDate = today.getTimeInMillis();

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar selectedCal = Calendar.getInstance();
            selectedCal.set(year, month, dayOfMonth, 0, 0, 0);
            selectedCal.set(Calendar.MILLISECOND, 0);

            // Lưu lại ngày được chọn
            selectedDate = selectedCal.getTimeInMillis();


            if (selectedCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    selectedCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                timePicker.setCurrentHour(today.get(Calendar.HOUR_OF_DAY));
                timePicker.setCurrentMinute(today.get(Calendar.MINUTE));
            } else {
                timePicker.setCurrentHour(7);
                timePicker.setCurrentMinute(0);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String date = sdf.format(selectedCal.getTime());
            System.out.println("Ngày được chọn: " + date);
        });




        // Thiết lập TimePicker
        timePicker.setIs24HourView(true); // Đặt chế độ 24 giờ
        timePicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)); // Đặt giờ hiện tại
        timePicker.setCurrentMinute(Calendar.getInstance().get(Calendar.MINUTE)); // Đặt phút hiện tại

        timePicker.setOnTimeChangedListener((view12, hourOfDay, minute) -> {
            // Kiểm tra nếu thời gian được chọn nằm ngoài giờ hoạt động
            if (!isOperatingHours(hourOfDay, minute)) {
                timePicker.setCurrentHour(7); // Đặt lại giờ thành 7 AM
                timePicker.setCurrentMinute(0);
                Toast.makeText(getActivity(), "Cửa hàng hoạt động từ 7:00 AM đến 10:00 PM.", Toast.LENGTH_SHORT).show();
            }
        });



        View.OnClickListener numberClickListener = v -> {
            resetNumberSelection();
            v.setSelected(true);
            v.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.selected_color));
            v.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.rounded_corners1));

            int id = v.getId();
            if (id == R.id.number1) {
                numberOfPeople = 1;
            } else if (id == R.id.number2) {
                numberOfPeople = 2;
            } else if (id == R.id.number3) {
                numberOfPeople = 3;
            } else if (id == R.id.number4) {
                numberOfPeople = 4;
            } else if (id == R.id.number5) {
                numberOfPeople = 5;
            } else if (id == R.id.number6) {
                numberOfPeople = 6;
            } else if (id == R.id.number7) {
                numberOfPeople = 7;
            } else if (id == R.id.number8) {
                numberOfPeople = 8;
            } else if (id == R.id.number9) {
                numberOfPeople = 9;
            } else if (id == R.id.number10) {
                numberOfPeople = 10;
            }
        };

        number1.setOnClickListener(numberClickListener);
        number2.setOnClickListener(numberClickListener);
        number3.setOnClickListener(numberClickListener);
        number4.setOnClickListener(numberClickListener);
        number5.setOnClickListener(numberClickListener);
        number6.setOnClickListener(numberClickListener);
        number7.setOnClickListener(numberClickListener);
        number8.setOnClickListener(numberClickListener);
        number9.setOnClickListener(numberClickListener);
        number10.setOnClickListener(numberClickListener);

        btnConfirm.setOnClickListener(v -> {

            textInputLayoutName.setError(null);
            textInputLayoutPhone.setError(null);
            textInputLayoutAddress.setError(null);
            textInputLayoutEmail.setError(null);


            // Lấy thông tin nhập từ người dùng
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();

            boolean hasError = false;

            //Kiểm tra số lượng người
            if (numberOfPeople == 0) {
                Toast.makeText(getActivity(), "Vui lòng chọn số lượng người.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Kiểm tra tên
            if (name.isEmpty()) {
                textInputLayoutName.setErrorEnabled(true);
                textInputLayoutName.setError("Vui lòng nhập tên.");
                hasError = true;
                return;
            } else if (name.length() < 3) {
                textInputLayoutName.setError("Tên tối thiểu 3 kí tự.");
                hasError = true;
                return;
            }
            // Kiểm tra số điện thoại
            if (phone.isEmpty()) {
                textInputLayoutPhone.setError("Vui lòng nhập số điện thoại.");
                hasError = true;
                return;
            } else if (phone.length() < 9 || phone.length() > 10|| !phone.toString().startsWith("0")) {
                textInputLayoutPhone.setError("Số điện thoại tối thiểu 9 số và đúng định dạng.");
                hasError = true;
                return;
            }
            // Kiểm tra địa chỉ
            if (address.isEmpty()) {
                textInputLayoutAddress.setError("Vui lòng nhập địa chỉ.");
                hasError = true;
                return;
            }
            // Kiểm tra email
            if (email.isEmpty()) {
                textInputLayoutEmail.setError("Vui lòng nhập email.");
                hasError = true;
                return;
            } else if (email.length() > 0 && !isValidEmail(email.toString())) {
                textInputLayoutEmail.setError("Vui lòng nhập email đúng định dạng.");
                hasError = true;
                return;
            }

            // Lấy thời gian hiện tại
            Calendar currentTime = Calendar.getInstance();
            currentTime.add(Calendar.HOUR_OF_DAY, 1);
            currentTime.set(Calendar.SECOND, 0);
            currentTime.set(Calendar.MILLISECOND, 0);

            // Tạo đối tượng Calendar từ ngày và giờ được chọn
            Calendar selectedDateTime = Calendar.getInstance();
            selectedDateTime.setTimeInMillis(selectedDate);
            selectedDateTime.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            selectedDateTime.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            selectedDateTime.set(Calendar.SECOND, 0);
            selectedDateTime.set(Calendar.MILLISECOND, 0);

            // In ra thông tin thời gian để debug
            SimpleDateFormat sdfDebug = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String currentTimeString = sdfDebug.format(currentTime.getTime());
            String selectedDateTimeString = sdfDebug.format(selectedDateTime.getTime());
            System.out.println("Thời gian hiện tại: " + currentTimeString);
            System.out.println("Thời gian được chọn: " + selectedDateTimeString);

            // So sánh thời gian và ngày được chọn với thời gian hiện tại
            if (selectedDateTime.before(currentTime)) {
                Toast.makeText(getActivity(), "Bạn cần đặt bàn trước 1 giờ để sắp xếp chỗ.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Xây dựng thông tin chi tiết đặt bàn
            SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String time = sdfTime.format(selectedDateTime.getTime());
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String date = sdfDate.format(selectedDateTime.getTime());

            // Kiểm tra đơn đặt bàn trùng lặp
            checkDuplicateReservation(phone, date, time, new OnDuplicateCheckListener() {
                @Override
                public void onResult(boolean isDuplicate) {
                    if (isDuplicate) {
                        Toast.makeText(getActivity(), "Đã có đơn đặt bàn trước đó.", Toast.LENGTH_LONG).show();
                    } else {
                        // Tiếp tục xử lý đặt bàn nếu không có đơn trùng lặp
                        showConfirmationDialog(name, phone, address, email, date, time, numberOfPeople, notes);
                    }
                }
            });
        });

        return view;
    }

    private void checkDuplicateReservation(String phone, String date, String time, OnDuplicateCheckListener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference reservationsRef = FirebaseDatabase.getInstance().getReference()
                    .child("users")
                    .child(currentUser.getUid())
                    .child("reservations");

            reservationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isDuplicate = false;
                    for (DataSnapshot reservationSnapshot : dataSnapshot.getChildren()) {
                        String existingPhone = reservationSnapshot.child("phone").getValue(String.class);
                        String existingDate = reservationSnapshot.child("date").getValue(String.class);
                        String existingTime = reservationSnapshot.child("time").getValue(String.class);

                        if (phone.equals(existingPhone) && date.equals(existingDate) && time.equals(existingTime)) {
                            isDuplicate = true;
                            break;
                        }
                    }
                    listener.onResult(isDuplicate);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.onResult(false); // Xử lý lỗi, giả định không có trùng lặp
                }
            });
        } else {
            listener.onResult(false); // Người dùng chưa đăng nhập, giả định không có trùng lặp
        }
    }

    private interface OnDuplicateCheckListener {
        void onResult(boolean isDuplicate);
    }

    private void showConfirmationDialog(String name, String phone, String address, String email,
                                        String date, String time, int numberOfPeople, String notes) {
        // Xây dựng chuỗi thông tin đặt bàn với định dạng HTML và xuống dòng
        String reservationDetails =
                "<b>Tên:</b> " + name + "<br/>" +
                        "<b>Số điện thoại:</b> " + phone + "<br/>" +
                        "<b>Địa chỉ:</b> " + address + "<br/>" +
                        "<b>Email:</b> " + email + "<br/>" +
                        "<b>Ngày:</b> " + date + "<br/>" +
                        "<b>Giờ:</b> " + time + "<br/>" +
                        "<b>Số lượng người:</b> " + numberOfPeople + "<br/>" +
                        "<b>Ghi chú:</b> " + (notes.isEmpty() ? "Không có ghi chú" : notes);

        // Tạo dialog tuỳ chỉnh từ custom_dialog.xml
        View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        TextView dialogTitle = dialogView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialogView.findViewById(R.id.dialogMessage);
        Button btnConfirmDialog = dialogView.findViewById(R.id.btnConfirmDialog);
        Button btnCancelDialog = dialogView.findViewById(R.id.btnCancelDialog);

        dialogTitle.setText("THÔNG TIN ĐẶT BÀN");
        dialogMessage.setText(Html.fromHtml(reservationDetails));

        // Xây dựng AlertDialog từ dialogView
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();

        // Xử lý khi người dùng chọn Xác nhận
        btnConfirmDialog.setOnClickListener(dialogButton -> {
            alertDialog.dismiss(); // Đóng dialog khi người dùng chọn Xác nhận
            saveReservationToFirebase(name, phone, address, email, date, time, numberOfPeople, notes);
        });

        // Xử lý khi người dùng chọn Hủy
        btnCancelDialog.setOnClickListener(dialogButton -> {
            alertDialog.dismiss(); // Đóng dialog khi người dùng chọn Hủy
        });

        alertDialog.show();
    }

    private void saveReservationToFirebase(String name, String phone, String address, String email,
                                           String date, String time, int numberOfPeople, String notes) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            DatabaseReference reservationRef = userRef.child("reservations").push();
            reservationRef.child("name").setValue(name);
            reservationRef.child("phone").setValue(phone);
            reservationRef.child("address").setValue(address);
            reservationRef.child("email").setValue(email);
            reservationRef.child("date").setValue(date);
            reservationRef.child("time").setValue(time);
            reservationRef.child("numberOfPeople").setValue(numberOfPeople);
            reservationRef.child("notes").setValue(notes);

            Toast.makeText(getActivity(), "Đặt bàn thành công.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getContext(), SuccessActivity.class);
            intent.putExtra("message", "Đặt bàn thành công");
            startActivity(intent);
        }
    }

    private void resetNumberSelection() {
        number1.setSelected(false);
        number2.setSelected(false);
        number3.setSelected(false);
        number4.setSelected(false);
        number5.setSelected(false);
        number6.setSelected(false);
        number7.setSelected(false);
        number8.setSelected(false);
        number9.setSelected(false);
        number10.setSelected(false);

        number1.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number2.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number3.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number4.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number5.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number6.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number7.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number8.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number9.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
        number10.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.hover_background));
    }

    // Phương thức kiểm tra thời gian có nằm trong khoảng hoạt động từ 7 AM đến 10 PM hay không
    private boolean isOperatingHours(int hourOfDay, int minute) {
        return hourOfDay >= 7 && hourOfDay < 21; // Kiểm tra giờ có từ 7 đến 22 (10 PM)
    }

    // Phương thức kiểm tra tính hợp lệ của email
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

}
