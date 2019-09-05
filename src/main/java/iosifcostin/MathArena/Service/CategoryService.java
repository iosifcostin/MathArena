package iosifcostin.MathArena.Service;

import iosifcostin.MathArena.model.Category;
import iosifcostin.MathArena.Repository.CategoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private CategoryRepository categoryRepo;

    public CategoryService(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }


    @Cacheable(value = "cache.allCategories")
    public List<Category> findAll (){return categoryRepo.findAll();}

   public Category findByName (String name){return categoryRepo.findByCategoryName(name);}

    public Category findById (Long id){return categoryRepo.findBygivenId(id);}


    @CacheEvict(value = {"cache.allCategories"}, allEntries = true)
    public void save (Category category){categoryRepo.save(category);}
    @CacheEvict(value = {"cache.allCategories"}, allEntries = true)
    public void edit (Category category, Long id){ categoryRepo.categoryUpdate(category,id);}
}
