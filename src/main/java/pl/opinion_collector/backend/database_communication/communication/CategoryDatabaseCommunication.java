package pl.opinion_collector.backend.database_communication.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.opinion_collector.backend.database_communication.model.Category;
import pl.opinion_collector.backend.database_communication.repository.CategoryRepository;
import pl.opinion_collector.backend.database_communication.utils.EntityPreUpdater;

import java.util.List;

@Component
@Transactional
public class CategoryDatabaseCommunication {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    EntityPreUpdater<Category> entityPreUpdater;

    public Category getCategoryByName(String categoryName) {
        return categoryRepository.getCategoryByCategoryName(categoryName);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category createCategory(String categoryName, Boolean visible) {
        Category category = new Category(categoryName, visible);
        return categoryRepository.save(category);
    }

    public void updateCategory(String categoryName, Boolean visible) {
        entityPreUpdater.saveOldData(getCategoryByName(categoryName));
        categoryRepository.updateCategory(categoryName, visible);
    }


    public void removeCategory(String categoryName) {
        entityPreUpdater.saveOldData(getCategoryByName(categoryName));
        categoryRepository.deleteByCategoryName(categoryName);
    }


}
