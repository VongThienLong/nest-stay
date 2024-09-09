package android.myapplication.Controller;

import android.app.Activity;
import android.content.Context;
import android.myapplication.Model.Reservation;
import android.myapplication.R;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {

    private Context context;
    private List<Reservation> reservationList;

    public ReservationAdapter(Context context, List<Reservation> reservationList) {
        this.context = context;
        this.reservationList = reservationList;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservationList.get(position);
        holder.bind(reservation);

        holder.btnCancelReservation.setOnClickListener(v -> {
            String reservationId = reservation.getId();
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(currentUser.getUid())
                        .child("reservations")
                        .child(reservationId);
                userRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Cập nhật danh sách và giao diện trên luồng chính
                        ((Activity) context).runOnUiThread(() -> {
                            // Kiểm tra xem chỉ số có hợp lệ không trước khi xóa
                            if (position >= 1 && position < reservationList.size()) {
                                reservationList.remove(position);
                                notifyItemRemoved(position);
                            } else {
                                // Xử lý trường hợp chỉ số không hợp lệ
                                Log.e("ReservationAdapter", "Invalid position: " + position);
                            }
                        });
                    } else {
                        // Xử lý lỗi
                        Log.e("ReservationAdapter", "Error removing reservation: " + task.getException().getMessage());
                    }
                });
            }
        });
    }



    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public class ReservationViewHolder extends RecyclerView.ViewHolder {

        private TextView tvReservationInfo;
        private Button btnCancelReservation;

        public ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReservationInfo = itemView.findViewById(R.id.tvReservationInfo);
            btnCancelReservation = itemView.findViewById(R.id.btnCancelReservation);
        }

        public void bind(Reservation reservation) {
            // Hiển thị chi tiết đặt chỗ với ID có màu đỏ
            String idHtml = "<font color='#FFAF20'>" + "<b>" + reservation.getDate() + "</b>" + " " + "<b>" + reservation.getTime() + "</b>" + "</font><br/>";
            String reservationDetails =
                    idHtml +
                            "<b>Email: </b>" + reservation.getEmail() + "<br/>" +
                            "<b>Tên: </b>" + reservation.getName() + "<br/>" +
                            "<b>Số điện thoại: </b>" + reservation.getPhone() + "<br/>" +
                            "<b>Địa chỉ: </b>" + reservation.getAddress() + "<br/>" +
                            "<b>Số lượng người: </b>" + reservation.getNumberOfPeople() + "<br/>" +
                            "<b>Ghi chú: </b>" + (reservation.getNotes() != null && !reservation.getNotes().isEmpty() ? reservation.getNotes() : "Không có ghi chú");

            // Cập nhật giao diện trên luồng chính
            ((Activity) context).runOnUiThread(() -> tvReservationInfo.setText(Html.fromHtml(reservationDetails, Html.FROM_HTML_MODE_COMPACT)));

            // Kiểm tra ngày giờ hiện tại
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime reservationDateTime = LocalDateTime.parse(reservation.getDate() + " " + reservation.getTime(), formatter);
            LocalDateTime now = LocalDateTime.now();

            // Ẩn nút hủy nếu ngày giờ đã qua
            if (reservationDateTime.isBefore(now)) {
                btnCancelReservation.setVisibility(View.GONE);
            } else {
                btnCancelReservation.setVisibility(View.VISIBLE);
            }
        }
    }
}
