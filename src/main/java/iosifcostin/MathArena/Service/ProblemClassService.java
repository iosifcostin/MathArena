package iosifcostin.MathArena.Service;


import iosifcostin.MathArena.Repository.ProblemClassRepository;
import iosifcostin.MathArena.model.ProblemClass;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemClassService {

    private ProblemClassRepository problemClassRepo;

    public ProblemClassService(ProblemClassRepository problemClassRepo) {
        this.problemClassRepo = problemClassRepo;
    }


    public ProblemClass findByName(String name){return problemClassRepo.findByProblemClassName(name);}

    public ProblemClass findById (Long id){return problemClassRepo.findBygivenId(id);}


    @Cacheable(value = "cache.allProblemClasses")
    public List<ProblemClass> findAll(){
        return problemClassRepo.findAll();
    }

    @CacheEvict(value = {"cache.allProblemClasses"}, allEntries = true)
    public ProblemClass save (ProblemClass problemClass){return problemClassRepo.save(problemClass);}

    @CacheEvict(value = {"cache.allProblemClasses"}, allEntries = true)
    public void edit (ProblemClass problemClass, Long id){ problemClassRepo.classUpdate(problemClass,id);}
}
