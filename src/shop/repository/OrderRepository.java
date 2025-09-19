package shop.repository;

import shop.model.Order;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    List<Order> findAll();
    Optional<Order> findById(int id);
    List<Order> findByUserId(int userId);
    void save(Order order);
    void update(Order order);
}