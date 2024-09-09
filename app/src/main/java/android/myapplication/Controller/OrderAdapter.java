package android.myapplication.Controller;

import android.content.Intent;
import android.myapplication.Model.ProductCategory;
import android.myapplication.R;
import android.myapplication.View.OrderItemActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrdersViewHolder> {

    private List<ProductCategory> ordersList;

    public OrderAdapter(List<ProductCategory> ordersList) {
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_grid_item, parent, false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        ProductCategory productCategory = ordersList.get(position);
        holder.categoryText.setText(productCategory.getName());

        if (productCategory.getImage() != null && !productCategory.getImage().isEmpty()) {
            Picasso.get()
                    .load(productCategory.getImage())
                    .error(R.drawable.placeholder) // Placeholder image if loading fails
                    .into(holder.categoryImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Image loaded successfully
                        }

                        @Override
                        public void onError(Exception e) {
                            // Handle error (e.g., log, show placeholder)
                            e.printStackTrace();
                        }
                    });
        } else {
            // Handle case where image URL is empty or null
            holder.categoryImage.setImageResource(R.drawable.placeholder);
        }

        holder.categoryImage.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), OrderItemActivity.class);
            intent.putExtra("productCategory", productCategory.getName());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText;
        ImageView categoryImage;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.categoryText);
            categoryImage = itemView.findViewById(R.id.categoryImage);

        }
    }
}
