package android.myapplication.Model;

public class PaymentOption {
    private String name;
    private int iconResId;

    public PaymentOption(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }
}
