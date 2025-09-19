package shop.model;

public class Product {
    private final int id;
    private final String name;
    private final double price;
    private final String manufacturer;
    private final Category category;
    private double rating;
    private int ratingCount;

    public Product(int id, String name, double price, String manufacturer, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.manufacturer = manufacturer;
        this.category = category;
        this.rating = 0;
        this.ratingCount = 0;
    }


    public void addRating(int rating) {
        double totalRating = this.rating * this.ratingCount + rating;
        this.ratingCount++;
        this.rating = totalRating / this.ratingCount;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getManufacturer() { return manufacturer; }
    public Category getCategory() { return category; }
    public double getRating() { return rating; }
    public int getRatingCount() { return ratingCount; }
}