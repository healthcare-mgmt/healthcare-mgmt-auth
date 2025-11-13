package com.hms.user.config;

import com.hms.user.entity.Role;
import com.hms.user.entity.enums.RoleName;
import com.hms.user.repo.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder {

    private final RoleRepository roleRepository;

    public DataSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void seedRoles() {
        List<RoleName> roleNames = Arrays.asList(RoleName.ADMIN, RoleName.DOCTOR, RoleName.PATIENT);

        for (RoleName roleName : roleNames) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                System.out.println("✅ Seeded role: " + roleName);
            }
        }
    }
}
