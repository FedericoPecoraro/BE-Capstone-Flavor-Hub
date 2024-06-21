package it.epicode.flavor_hub.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RolesService {

    @Autowired
    private RolesRepository rolesRepository;


    public Roles create(Roles roles){
        return rolesRepository.save(roles);
    }

    public boolean existsByRoleType(String roleType) {
        return rolesRepository.existsById(roleType);
    }
}
