package iosifcostin.MathArena.Repository;


import iosifcostin.MathArena.model.ProblemClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ProblemClassRepository extends JpaRepository<ProblemClass, Long> {

    ProblemClass findByProblemClassName(String name);

    @Query("SELECT pc FROM ProblemClass pc WHERE pc.id = :#{#id}")
    ProblemClass findBygivenId(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("UPDATE ProblemClass c SET c.problemClassName = :#{#problemClass.problemClassName} WHERE c.id = :#{#id}")
    void classUpdate(@Param("problemClass") ProblemClass problemClass, @Param("id") Long id);
}
