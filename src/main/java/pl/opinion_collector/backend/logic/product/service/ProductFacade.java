package pl.opinion_collector.backend.logic.product.service;

import pl.opinion_collector.backend.database_communication.model.Category;
import pl.opinion_collector.backend.database_communication.model.Product;
import pl.opinion_collector.backend.logic.product.service.wrapper.ProductWrapper;

import java.util.List;

public interface ProductFacade {

    Product getProductBySku(String sku);

    ProductWrapper getAllProducts(int page);

    ProductWrapper getProducts(int page);

    List<Product> getProductsFiltered(
            String categoryName,
            String searchPhrase,
            Integer opinionAvgMin,
            Integer opinionAvgMax
    );

    Product addProduct(
            Long authorId,
            String sku,
            String name,
            String pictureUrl,
            String description,
            List<String> categoryNames,
            Boolean visible
    );

    Product editProduct(Long authorId,
                     String sku,
                     String name,
                     String pictureUrl,
                     String description,
                     List<String> categoryNames,
                     Boolean visible
    );

    Product removeProduct(String sku);

    Category addCategory(String categoryName, Boolean visible);

    Category editCategory(String categoryName, Boolean visible);

    Category removeCategory(String categoryName);

    List<Category> getCategories();

    List<Category> getAllCategories();
}
