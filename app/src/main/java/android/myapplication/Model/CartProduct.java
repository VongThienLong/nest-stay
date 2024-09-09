package android.myapplication.Model;

public class CartProduct {
    private String name;
    private String image;
    private int price;
    private String quantity;
    private String description;

    public CartProduct() {
    }

    public CartProduct(String name, String description, String image, int price, String quantity) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public int getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDescription() {
        return description;
    }

}
