package shop.repository;

import shop.model.Order;
import java.util.*;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<Integer, Order> orders = new HashMap<>();
    private final Map<Integer, List<Order>> userOrders = new HashMap<>();

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public Optional<Order> findById(int id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public List<Order> findByUserId(int userId) {
        return userOrders.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public void save(Order order) {
        orders.put(order.getId(), order);
        userOrders.computeIfAbsent(order.getUserId(), k -> new ArrayList<>()).add(order);
    }

    @Override
    public void update(Order order) {
        orders.put(order.getId(), order);
        // Обновляем заказ в списке пользователя
        List<Order> userOrderList = userOrders.get(order.getUserId());
        if (userOrderList != null) {
            userOrderList.removeIf(o -> o.getId() == order.getId());
            userOrderList.add(order);
        }
    }
}