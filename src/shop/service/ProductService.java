package shop.service;

import shop.model.Product;
import shop.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> filterProducts(String keyword, Double minPrice, Double maxPrice, String manufacturer) {
        return productRepository.findAll().stream()
                .filter(product -> keyword == null || product.getName().toLowerCase().contains(keyword.toLowerCase()))
                .filter(product -> minPrice == null || product.getPrice() >= minPrice)
                .filter(product -> maxPrice == null || product.getPrice() <= maxPrice)
                .filter(product -> manufacturer == null || product.getManufacturer().equalsIgnoreCase(manufacturer))
                .collect(Collectors.toList());
    }

    public Product getProductById(int id) {
        return productRepository.findById(id).orElse(null);
    }

    public void rateProduct(int productId, int rating) {
        Product product = getProductById(productId);
        if (product != null) {
            product.addRating(rating);
        }
    }
}
