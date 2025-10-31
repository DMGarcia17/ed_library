package org.dmgarcia.app.infra;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static final EntityManagerFactory emf = build();

    private static EntityManagerFactory build() {
        try {
            EntityManagerFactory f = Persistence.createEntityManagerFactory("appPU");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (f.isOpen()) f.close();
            }));
            return f;
        } catch (Exception e) {
            System.err.println("Error inicializando JPA: " + e.getMessage());
            throw e;
        }
    }

    public static EntityManagerFactory getEMF() {
        return emf;
    }
}
