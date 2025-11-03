package org.dmgarcia.app.security;

import jakarta.persistence.EntityManager;
import org.dmgarcia.app.infra.JPAUtil;
import org.dmgarcia.app.model.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RoleRepository extends BaseRepository {

    public Optional<Role> find(String code) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try {
            return Optional.ofNullable(em.find(Role.class, code));
        } finally {
            em.close();
        }
    }

    public Optional<Role> findActive(String code) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try {
            var q = em.createQuery(
                    "select r from Role r where r.code = :c and r.deletedAt is null",
                    Role.class);
            q.setParameter("c", code);
            return q.getResultStream().findFirst();
        } finally {
            em.close();
        }
    }

    public List<Role> listActive() {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try {
            return em.createQuery(
                    "select r from Role r where r.deletedAt is null order by r.code",
                    Role.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Role save(Role r) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        return inTx(em, e -> e.merge(r));
    }

    public void softDelete(String code) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        inTx(em, e -> {
            Role r = e.find(Role.class, code);
            if (r != null && r.getDeletedAt() == null) {
                r.setDeletedAt(LocalDateTime.now());
            }
            return null;
        });
    }
}