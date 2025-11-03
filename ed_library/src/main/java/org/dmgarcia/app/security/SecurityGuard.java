package org.dmgarcia.app.security;

public final class SecurityGuard {
    public static void requireRole(SessionContext ctx, String roleCode){
        if (ctx == null || !ctx.hasRole(roleCode)){
            throw new SecurityException("Forbidden: missing role: "+roleCode);
        }
    }

    public static void requireAny(SessionContext ctx, String... roles) {
        for(String r : roles){
            if (ctx != null && ctx.hasRole(r)) return;
        }
        throw new SecurityException("Forbidden: none of required roles present");
    }

    private SecurityGuard() {}
}
