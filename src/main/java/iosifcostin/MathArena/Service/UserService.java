package iosifcostin.MathArena.Service;
import iosifcostin.MathArena.Repository.UserRepository;
import iosifcostin.MathArena.model.MathProblem;
import iosifcostin.MathArena.model.Role;
import iosifcostin.MathArena.model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
public class UserService {

    private UserRepository userRepository;
    private MathProblemService mathProblemService;
    private RoleService roleService;

    public UserService(UserRepository userRepository, MathProblemService mathProblemService, RoleService roleService) {
        this.userRepository = userRepository;
        this.mathProblemService = mathProblemService;
        this.roleService = roleService;
    }

    //
    @Cacheable(value = "cache.allUsers")
    public List<User> findAll(){return userRepository.findAll();}


    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByGoogleAuthId(String gAuthId) {
        return userRepository.findByGoogleAuthId(gAuthId);
    }

    public Page<User> findByIdPageable(Long id, Pageable pageRequest){
        Optional<User> user = userRepository.findById(id);
        List<User> users = user.map(Collections::singletonList).orElse(Collections.emptyList());
        return new PageImpl<>(users, pageRequest, users.size());
    }

    @Cacheable(value = "cache.allUsersPageable")
    public Page<User> findAllPageable(Pageable pageable) {
        return userRepository.findAll(pageable);


    }
    @Cacheable(value = "cache.byFirstNameContaining")
    public Page<User> findByFirstNameContaining(String username, Pageable pageable) {
        return userRepository.findByFirstNameContainingOrderByIdAsc(username, pageable);
    }

    @Cacheable(value = "cache.byLastNameContaining")
    public Page<User> findByLastNameContaining(String username, Pageable pageable) {
        return userRepository.findByLastNameContainingOrderByIdAsc(username, pageable);
    }

    public User findByIdEagerly (Long id){
        return userRepository.findByIdEagerly(id);
    }

    @Cacheable(value = "cache.byEmailContaining")
    public Page<User> findByEmailContaining(String email, Pageable pageable) {
        return userRepository.findByEmailContainingOrderByIdAsc(email, pageable);
    }
    @Cacheable(value = "cache.userById", key = "#id", unless="#result == null")
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User findByEmailEagerly(String email) {
        return userRepository.findByEmailEagerly(email);
    }
    public User findByEmailJoinFetchProblems(String email) {
        return userRepository.findByEmailJoinFetchProblems(email);
    }

    @CacheEvict(value = {"cache.allUsers","cache.allUsersPageable","cache.byEmailContaining",
            "cache.byFirstNameContaining","cache.byLastNameContaining","cache.userById"}, allEntries = true)
    public void setProfilePicture (Long id ,String path){userRepository.setProfilePicture(id,path);}


    @Transactional
    @CacheEvict(value = {"cache.allUsers"
//            , "cache.allProblemsPageable"
            ,"cache.allUsersPageable","cache.byEmailContaining",
            "cache.byEmailContaining","cache.byFirstNameContaining","cache.byLastNameContaining","cache.userById"}, allEntries = true)
    public void save(User user) {
        userRepository.save(user);
    }


    @CacheEvict(value = {"cache.allUsers"
//            , "cache.allProblemsPageable"
            ,"cache.allUsersPageable","cache.byEmailContaining",
            "cache.byEmailContaining","cache.byFirstNameContaining","cache.byLastNameContaining","cache.userById"}, allEntries = true)
    public void setProblems(User user) {
        userRepository.setProblems(user);
    }


    @CacheEvict(value = {"cache.allUsers"
//            ,"cache.allProblemsPageable"
            , "cache.allUsersPageable"
            ,"cache.byEmailContaining",
            "cache.byEmailContaining","cache.byFirstNameContaining","cache.byLastNameContaining","cache.userById"}, allEntries = true)
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }




    public List<MathProblem> getAssignedProblemsList(User user) {
        Map<Long, MathProblem> assignedProblemMap = new HashMap<>();

        List<MathProblem> problems = user.getMathProblems();
        for (MathProblem mp : problems) {
            assignedProblemMap.put(mp.getId(), mp);
        }

        List<MathProblem> userProblems = new ArrayList<>();
        List<MathProblem> allproblems = mathProblemService.findAll();
        for (MathProblem m : allproblems) {
            if (assignedProblemMap.containsKey(m.getId())) {
                userProblems.add(m);
            } else {
                userProblems.add(null);
            }
        }
        return userProblems;
    }


    public List<Role> getAssignedRolesList(User user) {
        Map<Long, Role> assignedRoleMap = new HashMap<>();
        List<Role> roles = user.getRoles();
        for (Role role : roles) {
            assignedRoleMap.put(role.getId(), role);
        }

        List<Role> userRoles = new ArrayList<>();
        List<Role> allRoles = roleService.findAll();
        for (Role role : allRoles) {
            if (assignedRoleMap.containsKey(role.getId())) {
                userRoles.add(role);
            } else {
                userRoles.add(null);
            }
        }
        return userRoles;
    }

}
