package iosifcostin.MathArena.Repository;

import iosifcostin.MathArena.model.MathProblem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MathProblemRepository extends JpaRepository<MathProblem,Long> {

    MathProblem findByName(String name);

    @Query("SELECT p FROM MathProblem p WHERE p.id = :#{#id}")
    MathProblem findBygivenId(@Param("id") Long id);

    Page<MathProblem> findAllByProblemClassIdAndCategoryId(Pageable pageable, Long classId, Long categoryId);

    Page<MathProblem> findByNameContainingOrderByIdAsc(String username, Pageable pageable);




    @Transactional
    @Modifying
    @Query("UPDATE MathProblem p SET p.name = :#{#problem.name}, p.category= :#{#problem.category}," +
            "p.description = :#{#problem.description}, p.result = :#{#problem.result}, " +
            "p.problemClass = :#{#problem.problemClass}, p.descriptionPath = :#{#problem.descriptionPath}," +
            "p.resultPath = :#{#problem.resultPath} WHERE p.id = :#{#id}")
    void problemUpdate(@Param("problem") MathProblem problem, @Param("id") Long id);

    @Transactional
    @Modifying
    @Query("DELETE  FROM  MathProblem p WHERE p.id = :#{#id}")
    void deleteById(@Param("id") Long id);
}
