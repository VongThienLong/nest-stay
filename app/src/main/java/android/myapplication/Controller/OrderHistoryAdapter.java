package android.myapplication.Controller;

import android.content.Intent;
import android.myapplication.Model.CartProduct;
import android.myapplication.Model.Order;
import android.myapplication.R;
import android.myapplication.View.OrderPaymentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>{

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private List<Order> orderList;
    public OrderHistoryAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Order order = orderList.get(position);
        holder.tvDate.setText(order.getTime());
        holder.tvTotal.setText(order.getTotalAmount()+ "đ");
        holder.tvName.setText(order.getName());
        holder.tvAddress.setText(order.getAddress());

        // Get number of item quantity from order
        int numberOfItem = 0;
        for (CartProduct product : order.getProducts()) {
            numberOfItem += Integer.parseInt(product.getQuantity());
        }
        holder.tvNumberOfItem.setText(numberOfItem + " món");


        holder.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCartAndGoToPayment(order, v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTotal, tvName, tvAddress, tvNumberOfItem;
        Button btnOrder;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvNumberOfItem = itemView.findViewById(R.id.tvNumberOfItem);
            btnOrder = itemView.findViewById(R.id.btnOrder);
        }
    }

    private void addToCartAndGoToPayment(Order order, View view) {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference cartRef = mDatabase.child("users").child(userId).child("cart");
        cartRef.removeValue();
        for (CartProduct product : order.getProducts()) {
            DatabaseReference newCartItemRef = cartRef.push();
            newCartItemRef.setValue(product);
        }

        Toast.makeText(view.getContext(), "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(view.getContext(), OrderPaymentActivity.class);
        view.getContext().startActivity(intent);
    }

}
