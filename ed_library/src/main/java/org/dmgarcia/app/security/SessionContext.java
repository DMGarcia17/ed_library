package org.dmgarcia.app.security;

import org.dmgarcia.app.model.User;

public class SessionContext {
    private final User user;

    public SessionContext(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public boolean hasRole(String roleCode){
        return user.getRoles().stream()
                .anyMatch(r->r.getCode().equalsIgnoreCase(roleCode));
    }

    public boolean isAdmin() { return hasRole("ADMIN"); }
}
