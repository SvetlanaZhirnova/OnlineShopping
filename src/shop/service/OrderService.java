package shop.service;

import shop.model.Order;
import shop.model.OrderStatus;
import shop.model.Product;
import shop.repository.OrderRepository;
import shop.util.Constants;

import java.time.LocalDateTime;
import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;
    private int nextOrderId = 1;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(int userId, List<Product> products) {
        Order order = new Order(nextOrderId++, userId, products);
        orderRepository.save(order);
        return order;
    }

    public List<Order> getUserOrders(int userId) {
        return orderRepository.findByUserId(userId);
    }

    public boolean cancelOrder(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && order.getStatus() == OrderStatus.PROCESSING) {
            order.updateStatus(OrderStatus.CANCELLED);
            orderRepository.update(order);
            return true;
        }
        return false;
    }

    public boolean returnOrder(int orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && order.getStatus() == OrderStatus.DELIVERED) {
            LocalDateTime returnDeadline = order.getOrderDate().plusDays(Constants.ORDER_EXPIRATION_DAYS);
            if (LocalDateTime.now().isBefore(returnDeadline)) {
                order.updateStatus(OrderStatus.RETURNED);
                orderRepository.update(order);
                return true;
            }
        }
        return false;
    }

    public Order repeatOrder(int orderId) {
        Order originalOrder = orderRepository.findById(orderId).orElse(null);
        if (originalOrder != null) {
            return createOrder(originalOrder.getUserId(), originalOrder.getProducts());
        }
        return null;
    }
}