package android.myapplication.Model;

import java.util.List;

public class Order {
    private String orderId;
    private List<CartProduct> products;
    private int totalAmount;
    private String deliveryOption;
    private String name;
    private String phone;
    private String address;
    private String note;
    private String time;

    public Order() {
    }

    public Order(String orderId, List<CartProduct> products, int totalAmount, String deliveryOption, String time, String name, String phone, String address, String note) {
        this.orderId = orderId;
        this.products = products;
        this.totalAmount = totalAmount;
        this.deliveryOption = deliveryOption;
        this.time = time;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.note = note;
    }


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<CartProduct> getProducts() {
        return products;
    }

    public void setProducts(List<CartProduct> products) {
        this.products = products;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getDeliveryOption() {
        return deliveryOption;
    }

    public void setDeliveryOption(String deliveryOption) {
        this.deliveryOption = deliveryOption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}