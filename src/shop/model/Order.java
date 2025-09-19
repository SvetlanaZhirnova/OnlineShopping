package shop.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private final int id;
    private final int userId;
    private final List<Product> products;
    private final double totalAmount;
    private OrderStatus status;
    private final LocalDateTime orderDate;

    public Order(int id, int userId, List<Product> products) {
        this.id = id;
        this.userId = userId;
        this.products = products;
        this.totalAmount = calculateTotalAmount();
        this.status = OrderStatus.PROCESSING;
        this.orderDate = LocalDateTime.now();
    }

    private double calculateTotalAmount() {
        return products.stream().mapToDouble(Product::getPrice).sum();
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }


    public int getId() { return id; }
    public int getUserId() { return userId; }
    public List<Product> getProducts() { return products; }
    public double getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getOrderDate() { return orderDate; }
}
