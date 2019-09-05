package iosifcostin.MathArena.Service;

import iosifcostin.MathArena.mathMl.MathMlToPng;
import iosifcostin.MathArena.model.MathProblem;
import iosifcostin.MathArena.Repository.MathProblemRepository;
import iosifcostin.MathArena.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MathProblemService {

    private MathProblemRepository mathProblemRepo;
    private MathMlToPng mathMlToPng;

    public MathProblemService(MathProblemRepository mathProblemRepo, MathMlToPng mathMlToPng) {
        this.mathProblemRepo = mathProblemRepo;
        this.mathMlToPng = mathMlToPng;
    }

//        @Cacheable(value = "cache.allProblemsPageable")
    public Page<MathProblem> findAllPageable(Pageable pageable) {
        return mathProblemRepo.findAll(pageable);
    }

    @Cacheable(value = "cache.allProblems")
    public List<MathProblem> findAll() {
        return mathProblemRepo.findAll();
    }


    public Page<MathProblem> findAllByClassAndCategory(Pageable pageable, Long classId, Long categoryId) {
        return mathProblemRepo.findAllByProblemClassIdAndCategoryId(pageable, classId, categoryId);
    }


    public MathProblem findByName(String name) {
        return mathProblemRepo.findByName(name);
    }


    @Cacheable(value = "cache.MathProblemById")
    public MathProblem findById(Long id) {
        return mathProblemRepo.findBygivenId(id);
    }

    @CacheEvict(value = {"cache.allProblems"
//            ,"cache.allProblemsPageable"
            ,"cache.MathProblemById"}, allEntries = true)
    public void save(MathProblem problem) {
        mathProblemRepo.save(problem);
    }

    @CacheEvict(value = {"cache.allProblems"
//            ,"cache.allProblemsPageable"
            ,"cache.MathProblemById"}, allEntries = true)
    public void edit(MathProblem mathProblem, Long id) {
        mathProblemRepo.problemUpdate(mathProblem, id);
    }

    @CacheEvict(value = {"cache.allProblems"
//            , "cache.allProblemsPageable"
            ,"cache.MathProblemById"}, allEntries = true)
    public void deleteById(Long id) {
        mathProblemRepo.deleteById(id);
    }


    public Page<MathProblem> findByIdPageable(Long id, Pageable pageRequest) {
        Optional<MathProblem> mathProblem = mathProblemRepo.findById(id);
        List<MathProblem> mathProblems = mathProblem.map(Collections::singletonList).orElse(Collections.emptyList());
        return new PageImpl<>(mathProblems, pageRequest, mathProblems.size());
    }

    public Page<MathProblem> findByNameContaining(String username, Pageable pageable) {
        return mathProblemRepo.findByNameContainingOrderByIdAsc(username, pageable);
    }



}
