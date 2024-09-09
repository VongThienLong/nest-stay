package android.myapplication.Model;


import java.io.Serializable;

public class AddressProfile implements Serializable {
    private String id;
    private String name;
    private String address;
    private String phone;
    private String notes;

    public AddressProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(AddressProfile.class)
    }

    public AddressProfile(String id, String name, String address, String phone, String notes) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
