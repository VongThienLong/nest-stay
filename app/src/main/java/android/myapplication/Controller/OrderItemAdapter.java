package android.myapplication.Controller;

import android.content.Intent;
import android.myapplication.Model.Product;
import android.myapplication.R;
import android.myapplication.View.ItemDescriptionActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private List<Product> orderItemList;

    public OrderItemAdapter(List<Product> orderItemList) {
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        Product orderItem = orderItemList.get(position);
        holder.orderItemText.setText(orderItem.getName());

        Log.d("OrderItemAdapter", "onBindViewHolder: " + orderItem.getImage());
        if (orderItem.getImage() != null && !orderItem.getImage().isEmpty()) {
            Picasso.get()
                    .load(orderItem.getImage())
                    .error(R.drawable.placeholder) // Placeholder image if loading fails
                    .into(holder.orderItemImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
        } else {
            // Handle case where image URL is empty or null
            holder.orderItemImage.setImageResource(R.drawable.placeholder);
        }

        holder.orderItemPrice.setText(orderItem.getPrice() + "đ");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ItemDescriptionActivity.class);
                intent.putExtra("name", orderItem.getName());
                intent.putExtra("price", orderItem.getPrice()+"");
                intent.putExtra("image", orderItem.getImage());
                intent.putExtra("description", orderItem.getDescription());

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderItemList == null ? 0 : orderItemList.size();
    }

    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView orderItemText, orderItemPrice;
        ImageView orderItemImage;
        ImageButton addButton;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            orderItemText = itemView.findViewById(R.id.itemName);
            orderItemPrice = itemView.findViewById(R.id.itemPrice);
            orderItemImage = itemView.findViewById(R.id.itemImageView);
            addButton = itemView.findViewById(R.id.plus_icon);
        }
    }
}