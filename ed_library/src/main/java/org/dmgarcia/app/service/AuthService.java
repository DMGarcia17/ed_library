package org.dmgarcia.app.service;

import org.dmgarcia.app.model.Role;
import org.dmgarcia.app.model.User;
import org.dmgarcia.app.security.RoleRepository;
import org.dmgarcia.app.security.SessionContext;
import org.dmgarcia.app.security.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Set;

public class AuthService {
    private final UserRepository userRepo = new UserRepository();
    private final RoleRepository roleRepo = new RoleRepository();

    public SessionContext login(String username, String passwordPlain) {
        var opt = userRepo.findActiveWithRoles(username);
        if (opt.isEmpty()) return null;
        var user = opt.get();
        if(!BCrypt.checkpw(passwordPlain, user.getPasswordHash())) return null;
        return new SessionContext(user);
    }

    public User createUser(String username, String passwordPlain,
                           String firstName, String lastName, Set<String> roleCodes){
        User u = new User();
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setUsername(username);
        u.setPasswordHash(BCrypt.hashpw(passwordPlain, BCrypt.gensalt()));
        u.setLastUpdatePassword(LocalDateTime.now());

        for(String code : roleCodes) {
            Role r = roleRepo.findActive(code)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found: "+code));
            u.getRoles().add(r);
        }
        return userRepo.save(u);
    }
}
