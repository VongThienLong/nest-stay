package android.myapplication.Controller;
import android.myapplication.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PaymentMethodAdapter extends BaseAdapter {

    private Context context;
    private String[] paymentMethods;
    private int[] paymentIcons;
    private LayoutInflater inflater;

    public PaymentMethodAdapter(Context context, String[] paymentMethods, int[] paymentIcons) {
        this.context = context;
        this.paymentMethods = paymentMethods;
        this.paymentIcons = paymentIcons;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return paymentMethods.length;
    }

    @Override
    public Object getItem(int position) {
        return paymentMethods[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.textView = convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setImageResource(paymentIcons[position]);
        holder.textView.setText(paymentMethods[position]);

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}
