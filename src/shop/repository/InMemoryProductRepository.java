package shop.repository;

import shop.model.Category;
import shop.model.Product;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<Integer, Product> products = new HashMap<>();
    private int nextId = 1;

    public InMemoryProductRepository() {
        initializeSampleData();
    }

    private void initializeSampleData() {
        save(new Product(nextId++, "iPhone 13", 799.99, "Apple", Category.ELECTRONICS));
        save(new Product(nextId++, "MacBook Pro", 1299.99, "Apple", Category.ELECTRONICS));
        save(new Product(nextId++, "Java Programming", 49.99, "O'Reilly", Category.BOOKS));
        save(new Product(nextId++, "Football", 29.99, "Nike", Category.SPORTS));
        save(new Product(nextId++, "T-Shirt", 19.99, "Adidas", Category.CLOTHING));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public Optional<Product> findById(int id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findByCategory(String category) {
        return findAll().stream()
                .filter(product -> product.getCategory().name().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByPriceRange(double minPrice, double maxPrice) {
        return findAll().stream()
                .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByManufacturer(String manufacturer) {
        return findAll().stream()
                .filter(product -> product.getManufacturer().equalsIgnoreCase(manufacturer))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Product product) {
        products.put(product.getId(), product);
    }
}