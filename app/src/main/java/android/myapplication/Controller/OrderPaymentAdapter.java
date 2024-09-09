package android.myapplication.Controller;

import android.content.Intent;
import android.myapplication.Model.CartProduct;
import android.myapplication.R;
import android.myapplication.View.ItemDescriptionActivity;
import android.myapplication.View.OrderPaymentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderPaymentAdapter extends RecyclerView.Adapter<OrderPaymentAdapter.OrderPaymentViewHolder> {
    private List<CartProduct> orderItemList;

    public OrderPaymentAdapter(List<CartProduct> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public OrderPaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new OrderPaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderPaymentViewHolder holder, int position) {
        CartProduct orderItem = orderItemList.get(position);
        holder.productName.setText(orderItem.getName());
        holder.productPrice.setText(orderItem.getPrice()+"đ");
        holder.productQuantity.setText(orderItem.getQuantity() + "x");

        holder.itemView.setOnClickListener(v -> {
            // set item click to show product detail
            Intent intent = new Intent(v.getContext(), ItemDescriptionActivity.class);
            intent.putExtra("name", orderItem.getName());
            intent.putExtra("description", orderItem.getDescription());
            intent.putExtra("image", orderItem.getImage());
            intent.putExtra("price", orderItem.getPrice()+"");
            intent.putExtra("quantity", orderItem.getQuantity());
            ((OrderPaymentActivity) v.getContext()).startActivityForResult(intent, 1);
        });
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public static class OrderPaymentViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productQuantity;

        public OrderPaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productQuantity = itemView.findViewById(R.id.productQuantity);
        }
    }

    public void updateCartProducts(List<CartProduct> newCartProductList) {
        Log.d("OrderPaymentAdapter", "Updating cart products: " + newCartProductList.size());
        this.orderItemList.clear();
        this.orderItemList.addAll(newCartProductList);
        notifyDataSetChanged();
    }
}
