package shop.service;

import shop.model.Product;
import java.util.List;
import java.util.stream.Collectors;

public class RecommendationService {
    private final ProductService productService;

    public RecommendationService(ProductService productService) {
        this.productService = productService;
    }

    public List<Product> getRecommendedProducts(List<Product> userOrderHistory) {
        if (userOrderHistory.isEmpty()) {
            return getPopularProducts();
        }


        String mostCommonCategory = findMostCommonCategory(userOrderHistory);

        return productService.getAllProducts().stream()
                .filter(product -> product.getCategory().name().equals(mostCommonCategory))
                .filter(product -> !userOrderHistory.contains(product))
                .sorted((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()))
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<Product> getPopularProducts() {
        return productService.getAllProducts().stream()
                .filter(product -> product.getRatingCount() > 0)
                .sorted((p1, p2) -> Double.compare(p2.getRating(), p1.getRating()))
                .limit(5)
                .collect(Collectors.toList());
    }

    private String findMostCommonCategory(List<Product> products) {
        return products.stream()
                .collect(Collectors.groupingBy(product -> product.getCategory().name(), Collectors.counting()))
                .entrySet().stream()
                .max((e1, e2) -> Long.compare(e1.getValue(), e2.getValue()))
                .map(entry -> entry.getKey())
                .orElse("ELECTRONICS");
    }
}
