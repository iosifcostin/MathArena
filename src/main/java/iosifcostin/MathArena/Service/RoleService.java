package iosifcostin.MathArena.Service;

import iosifcostin.MathArena.Repository.RoleRepository;
import iosifcostin.MathArena.model.Role;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    @Cacheable("cache.allRoles")
    public List<Role> findAll() {
        return roleRepository.findAll();
    }
    @CacheEvict(value = {"cache.allRoles" , "cache.roleByName", "cache.roleById"}, allEntries = true)
    public void save(Role role) {
        roleRepository.save(role);
    }

    @Cacheable(value = "cache.roleByName", key = "#name", unless = "#result == null")
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }

    @Cacheable(value = "cache.roleById", key = "#id", unless = "#result == null")
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

}
