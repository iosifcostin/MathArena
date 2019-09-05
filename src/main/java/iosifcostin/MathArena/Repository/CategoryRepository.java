package iosifcostin.MathArena.Repository;

import iosifcostin.MathArena.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByCategoryName(String name);


    @Query("SELECT c FROM Category c WHERE c.id = :#{#id}")
    Category findBygivenId(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Category c SET c.categoryName = :#{#category.categoryName} WHERE c.id = :#{#id}")
    void categoryUpdate(@Param("category") Category category, @Param("id") Long id);

}
