package android.myapplication.Model;

public class Reservation {
    private String id;
    private String name;
    private String phone;
    private String address;
    private String email;
    private String date;
    private String time;
    private int numberOfPeople;
    private String notes;

    public Reservation(String id, String name, String phone, String address, String email, String date, String time, int numberOfPeople, String notes) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.email = email;
        this.date = date;
        this.time = time;
        this.numberOfPeople = numberOfPeople;
        this.notes = notes;
    }
    public Reservation() {
        // Default constructor required for Firebase
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

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public String getNotes() {
        return notes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
