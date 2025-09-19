package shop;

import shop.model.*;
import shop.repository.*;
import shop.service.*;
import shop.util.Constants;

import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static ProductService productService;
    private static OrderService orderService;
    private static RecommendationService recommendationService;
    private static List<Product> shoppingCart = new ArrayList<>();
    private static int currentUserId = 1; // Для демонстрации
    private static int nextOrderId = 1;

    public static void main(String[] args) {
        initializeServices();
        showMainMenu();
    }

    private static void initializeServices() {
        ProductRepository productRepository = new InMemoryProductRepository();
        OrderRepository orderRepository = new InMemoryOrderRepository();

        productService = new ProductService(productRepository);
        orderService = new OrderService(orderRepository);
        recommendationService = new RecommendationService(productService);
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n=== МАГАЗИН ===");
            System.out.println("1. Просмотреть товары");
            System.out.println("2. Фильтровать товары");
            System.out.println("3. Мои заказы");
            System.out.println("4. Рекомендации");
            System.out.println("5. Корзина (" + shoppingCart.size() + " товаров)");
            System.out.println("6. Выход");
            System.out.print("Выберите опцию: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1 -> showProducts();
                case 2 -> filterProducts();
                case 3 -> showUserOrders();
                case 4 -> showRecommendations();
                case 5 -> showShoppingCart();
                case 6 -> { return; }
                default -> System.out.println("Неверный выбор!");
            }
        }
    }

    private static void showProducts() {
        List<Product> products = productService.getAllProducts();
        displayProducts(products);

        System.out.println("\n1. Добавить в корзину");
        System.out.println("2. Оценить товар");
        System.out.println("3. Назад");
        System.out.print("Выберите опцию: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            addToCart();
        } else if (choice == 2) {
            rateProduct();
        }
    }

    private static void addToCart() {
        System.out.print("Введите ID товара: ");
        int productId = scanner.nextInt();
        scanner.nextLine();

        Product product = productService.getProductById(productId);
        if (product != null) {
            shoppingCart.add(product);
            System.out.println("Товар '" + product.getName() + "' добавлен в корзину!");
        } else {
            System.out.println("Товар с ID " + productId + " не найден!");
        }
    }

    private static void rateProduct() {
        System.out.print("Введите ID товара: ");
        int productId = scanner.nextInt();
        System.out.print("Введите оценку (1-5): ");
        int rating = scanner.nextInt();
        scanner.nextLine();

        if (rating >= Constants.MIN_PRODUCT_RATING && rating <= Constants.MAX_PRODUCT_RATING) {
            productService.rateProduct(productId, rating);
            System.out.println("Спасибо за оценку!");
        } else {
            System.out.println("Неверная оценка! Допустимые значения: 1-5");
        }
    }

    private static void filterProducts() {
        System.out.println("\n=== ФИЛЬТРАЦИЯ ТОВАРОВ ===");

        System.out.print("Ключевое слово (или Enter для пропуска): ");
        String keyword = scanner.nextLine().trim();
        if (keyword.isEmpty()) keyword = null;

        System.out.print("Минимальная цена (или Enter для пропуска): ");
        String minPriceInput = scanner.nextLine().trim();
        Double minPrice = minPriceInput.isEmpty() ? null : Double.parseDouble(minPriceInput);

        System.out.print("Максимальная цена (или Enter для пропуска): ");
        String maxPriceInput = scanner.nextLine().trim();
        Double maxPrice = maxPriceInput.isEmpty() ? null : Double.parseDouble(maxPriceInput);

        System.out.print("Производитель (или Enter для пропуска): ");
        String manufacturer = scanner.nextLine().trim();
        if (manufacturer.isEmpty()) manufacturer = null;

        List<Product> filteredProducts = productService.filterProducts(keyword, minPrice, maxPrice, manufacturer);

        if (filteredProducts.isEmpty()) {
            System.out.println("Товары по вашему запросу не найдены!");
        } else {
            System.out.println("\n=== РЕЗУЛЬТАТЫ ПОИСКА ===");
            displayProducts(filteredProducts);

            System.out.println("\n1. Добавить товар в корзину");
            System.out.println("2. Новый поиск");
            System.out.println("3. Назад в меню");
            System.out.print("Выберите опцию: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                addToCartFromFilter(filteredProducts);
            } else if (choice == 2) {
                filterProducts();
            }
        }
    }

    private static void addToCartFromFilter(List<Product> products) {
        System.out.print("Введите ID товара из списка: ");
        int productId = scanner.nextInt();
        scanner.nextLine();

        Product selectedProduct = products.stream()
                .filter(p -> p.getId() == productId)
                .findFirst()
                .orElse(null);

        if (selectedProduct != null) {
            shoppingCart.add(selectedProduct);
            System.out.println("Товар '" + selectedProduct.getName() + "' добавлен в корзину!");
        } else {
            System.out.println("Товар с ID " + productId + " не найден в результатах поиска!");
        }
    }

    private static void showUserOrders() {
        List<Order> userOrders = orderService.getUserOrders(currentUserId);

        if (userOrders.isEmpty()) {
            System.out.println("У вас пока нет заказов!");
            return;
        }

        System.out.println("\n=== ВАШИ ЗАКАЗЫ ===");
        for (Order order : userOrders) {
            System.out.printf("Заказ #%d - %.2f ₽ - %s - %s%n",
                    order.getId(),
                    order.getTotalAmount(),
                    order.getStatus().getDisplayName(),
                    order.getOrderDate());

            System.out.println("Товары:");
            for (Product product : order.getProducts()) {
                System.out.printf("  - %s (%.2f ₽)%n", product.getName(), product.getPrice());
            }
            System.out.println();
        }

        System.out.println("1. Повторить заказ");
        System.out.println("2. Вернуть заказ");
        System.out.println("3. Отменить заказ");
        System.out.println("4. Назад");
        System.out.print("Выберите опцию: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice >= 1 && choice <= 3) {
            System.out.print("Введите номер заказа: ");
            int orderId = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> repeatOrder(orderId);
                case 2 -> returnOrder(orderId);
                case 3 -> cancelOrder(orderId);
            }
        }
    }

    private static void repeatOrder(int orderId) {
        Order repeatedOrder = orderService.repeatOrder(orderId);
        if (repeatedOrder != null) {
            System.out.println("Заказ #" + repeatedOrder.getId() + " создан повторно!");
        } else {
            System.out.println("Не удалось повторить заказ!");
        }
    }

    private static void returnOrder(int orderId) {
        boolean success = orderService.returnOrder(orderId);
        if (success) {
            System.out.println("Заказ #" + orderId + " возвращен!");
        } else {
            System.out.println("Не удалось вернуть заказ! Проверьте статус заказа.");
        }
    }

    private static void cancelOrder(int orderId) {
        boolean success = orderService.cancelOrder(orderId);
        if (success) {
            System.out.println("Заказ #" + orderId + " отменен!");
        } else {
            System.out.println("Не удалось отменить заказ! Можно отменять только заказы в обработке.");
        }
    }

    private static void showRecommendations() {
        List<Order> userOrders = orderService.getUserOrders(currentUserId);
        List<Product> orderHistory = userOrders.stream()
                .flatMap(order -> order.getProducts().stream())
                .toList();

        List<Product> recommendations = recommendationService.getRecommendedProducts(orderHistory);

        if (recommendations.isEmpty()) {
            System.out.println("Пока нет рекомендаций для вас!");
            return;
        }

        System.out.println("\n=== РЕКОМЕНДАЦИИ ДЛЯ ВАС ===");
        displayProducts(recommendations);

        System.out.println("\n1. Добавить в корзину");
        System.out.println("2. Обновить рекомендации");
        System.out.println("3. Назад");
        System.out.print("Выберите опцию: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            addRecommendedToCart(recommendations);
        } else if (choice == 2) {
            showRecommendations();
        }
    }

    private static void addRecommendedToCart(List<Product> recommendations) {
        System.out.print("Введите ID товара из рекомендаций: ");
        int productId = scanner.nextInt();
        scanner.nextLine();

        Product selectedProduct = recommendations.stream()
                .filter(p -> p.getId() == productId)
                .findFirst()
                .orElse(null);

        if (selectedProduct != null) {
            shoppingCart.add(selectedProduct);
            System.out.println("Товар '" + selectedProduct.getName() + "' добавлен в корзину!");
        } else {
            System.out.println("Товар с ID " + productId + " не найден в рекомендациях!");
        }
    }

    private static void showShoppingCart() {
        if (shoppingCart.isEmpty()) {
            System.out.println("Корзина пуста!");
            return;
        }

        System.out.println("\n=== ВАША КОРЗИНА ===");
        double total = 0;
        for (int i = 0; i < shoppingCart.size(); i++) {
            Product product = shoppingCart.get(i);
            System.out.printf("%d. %s - %.2f ₽%n", i + 1, product.getName(), product.getPrice());
            total += product.getPrice();
        }
        System.out.printf("Итого: %.2f ₽%n", total);

        System.out.println("\n1. Оформить заказ");
        System.out.println("2. Удалить товар");
        System.out.println("3. Очистить корзину");
        System.out.println("4. Назад");
        System.out.print("Выберите опцию: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> createOrderFromCart();
            case 2 -> removeFromCart();
            case 3 -> clearCart();
        }
    }

    private static void createOrderFromCart() {
        if (shoppingCart.isEmpty()) {
            System.out.println("Корзина пуста!");
            return;
        }

        Order order = orderService.createOrder(currentUserId, new ArrayList<>(shoppingCart));
        System.out.println("Заказ #" + order.getId() + " создан успешно!");
        shoppingCart.clear();
    }

    private static void removeFromCart() {
        System.out.print("Введите номер товара для удаления: ");
        int itemNumber = scanner.nextInt();
        scanner.nextLine();

        if (itemNumber > 0 && itemNumber <= shoppingCart.size()) {
            Product removedProduct = shoppingCart.remove(itemNumber - 1);
            System.out.println("Товар '" + removedProduct.getName() + "' удален из корзины!");
        } else {
            System.out.println("Неверный номер товара!");
        }
    }

    private static void clearCart() {
        shoppingCart.clear();
        System.out.println("Корзина очищена!");
    }

    private static void displayProducts(List<Product> products) {
        System.out.println("\n=== ТОВАРЫ ===");
        for (Product product : products) {
            System.out.printf("%d. %s - %.2f ₽ (%s) ★%.1f (%d оценок)%n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getManufacturer(),
                    product.getRating(),
                    product.getRatingCount());
        }
    }
}