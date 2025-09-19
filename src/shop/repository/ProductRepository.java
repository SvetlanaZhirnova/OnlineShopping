package shop.repository;

import shop.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll();
    Optional<Product> findById(int id);
    List<Product> findByCategory(String category);
    List<Product> findByPriceRange(double minPrice, double maxPrice);
    List<Product> findByManufacturer(String manufacturer);
    void save(Product product);
}
