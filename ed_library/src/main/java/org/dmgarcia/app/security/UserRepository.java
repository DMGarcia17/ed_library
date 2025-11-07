package org.dmgarcia.app.security;

import jakarta.persistence.EntityManager;
import org.dmgarcia.app.infra.JPAUtil;
import org.dmgarcia.app.model.Role;
import org.dmgarcia.app.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class UserRepository extends BaseRepository {
    public Optional<User> findActiveByUsername(String username){
        try (EntityManager em = JPAUtil.getEMF().createEntityManager()) {
            var q = em.createQuery("select u from User u where u.username = :u", User.class);
            q.setParameter("u", username);
            return q.getResultStream().findFirst();
        }
    }

    public Optional<User> findActiveWithRoles(String username){
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try{
            var q = em.createQuery(
                    "select distinct u from User u " +
                            "left join fetch u.roles r " +
                            "where u.username = :u and u.deletedAt is null " +
                            "and (r.deletedAt is null or r is null)", User.class);
            q.setParameter("u", username);
            return q.getResultStream().findFirst();
        } finally {
            em.close();
        }
    }

    public boolean existsActive(String username){
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try{
            Long c = em.createQuery("select count(u) from User u " +
                    "where u.username = :u and u.deletedAt is null", Long.class)
                    .setParameter("u", username)
                    .getSingleResult();
            return c!=null && c>0;

        }finally {
            em.close();
        }
    }

    public List<User> listActive(){
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try{
            return em.createQuery("select u from User u where u.deletedAt is null order by u.username"
                    , User.class).getResultList();
        }finally {
            em.close();
        }
    }

    public List<User> findAllWithRoles(){
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try{
            return em.createQuery("select distinct u from User u left join fetch u.roles where u.deletedAt is null order by u.username"
                    , User.class).getResultList();
        }finally {
            em.close();
        }
    }

    public List<User> searchActiveByName(String term) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        try {
            String like = "%" + term.toLowerCase() + "%";
            return em.createQuery(
                            "select u from User u " +
                                    "where u.deletedAt is null and (" +
                                    " lower(u.firstName) like :t or lower(u.lastName) like :t or lower(u.username) like :t ) " +
                                    "order by u.username", User.class)
                    .setParameter("t", like)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /** Inserta o actualiza (merge) usuario (sin tocar roles). */
    public User save(User u) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        return inTx(em, e -> e.merge(u));
    }

    /** Soft delete */
    public void softDelete(String username) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        inTx(em, e -> {
            User u = e.find(User.class, username);
            if (u != null && u.getDeletedAt() == null) {
                u.setDeletedAt(LocalDateTime.now());
            }
            return null;
        });
    }

    /** Reemplaza completamente el set de roles de un usuario */
    public void replaceRoles(String username, Set<Role> newRoles) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        inTx(em, e -> {
            User u = e.find(User.class, username);
            if (u == null) throw new IllegalArgumentException("User not found: " + username);
            // limpiar y asignar (asegura que las entidades Role estén gestionadas)
            u.getRoles().clear();
            for (Role r : newRoles) {
                Role managed = e.getReference(Role.class, r.getCode());
                u.getRoles().add(managed);
            }
            return null;
        });
    }

    /** Agrega un rol específico si no existe ya */
    public void addRole(String username, String roleCode) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        inTx(em, e -> {
            User u = e.find(User.class, username);
            if (u == null) throw new IllegalArgumentException("User not found: " + username);
            Role r = e.find(Role.class, roleCode);
            if (r == null) throw new IllegalArgumentException("Role not found: " + roleCode);
            u.getRoles().add(r);
            return null;
        });
    }

    /** Quita un rol específico */
    public void removeRole(String username, String roleCode) {
        EntityManager em = JPAUtil.getEMF().createEntityManager();
        inTx(em, e -> {
            User u = e.find(User.class, username);
            if (u == null) throw new IllegalArgumentException("User not found: " + username);
            u.getRoles().removeIf(r -> r.getCode().equalsIgnoreCase(roleCode));
            return null;
        });
    }
}
