package android.myapplication.Controller;
import android.myapplication.Model.AddressProfile;
import android.myapplication.View.AddressActivity;

import android.myapplication.R;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.List;


public class AddressProfileAdapter extends RecyclerView.Adapter<AddressProfileAdapter.ViewHolder> {
    List<AddressProfile> addressProfileList;

    public AddressProfileAdapter(List<AddressProfile> addressProfileList) {
        this.addressProfileList = addressProfileList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_profile_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressProfileAdapter.ViewHolder holder, int position) {
        AddressProfile addressProfile = addressProfileList.get(position);
        holder.name.setText(addressProfile.getName());
        holder.address.setText(addressProfile.getAddress());
        holder.phone.setText(addressProfile.getPhone());

        holder.edit.setOnClickListener(v -> {
            String profileId = addressProfileList.get(holder.getAdapterPosition()).getId();
            Intent intent = new Intent(v.getContext(), AddressActivity.class);
            intent.putExtra("PROFILE_ID", profileId);
            v.getContext().startActivity(intent);
        });

        holder.delete.setOnClickListener(v -> {
            int profilePosition = holder.getAdapterPosition();
            if (profilePosition != RecyclerView.NO_POSITION) { // Check if position is valid
                String profileId = addressProfileList.get(profilePosition).getId();
                DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("addressProfiles")
                        .child(profileId);
                profileRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Notify the adapter to refresh the list or remove the item from the list
                        addressProfileList.remove(profilePosition);
                        notifyItemRemoved(profilePosition);
                        Toast.makeText(v.getContext(), "Đã xóa hồ sơ giao hàng", Toast.LENGTH_SHORT).show();
                    } else {
                        // Handle failure
                        Toast.makeText(v.getContext(), "Xóa hồ sơ giao hàng thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("selectedAddressProfile", (Serializable) addressProfileList.get(holder.getAdapterPosition()));
            ((Activity) v.getContext()).setResult(Activity.RESULT_OK, returnIntent);
            ((Activity) v.getContext()).finish();
        });
    }

    @Override
    public int getItemCount() {
        return addressProfileList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, phone;
        ImageButton edit, delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            address = itemView.findViewById(R.id.tvAddress);
            phone = itemView.findViewById(R.id.tvPhone);
            delete = itemView.findViewById(R.id.btnDelete);
            edit = itemView.findViewById(R.id.btnEdit);
        }
    }



}
